package com.yuanno.block_clover.spells.antimagic;

import com.yuanno.block_clover.api.Beapi;
import com.yuanno.block_clover.api.ability.AbilityCategories;
import com.yuanno.block_clover.api.ability.AbilityCore;
import com.yuanno.block_clover.api.ability.AbilityDamageKind;
import com.yuanno.block_clover.api.ability.AbilityHelper;
import com.yuanno.block_clover.api.ability.interfaces.IParallelContinuousAbility;
import com.yuanno.block_clover.api.ability.sorts.ContinuousAbility;
import com.yuanno.block_clover.blocks.tileentities.AntiMagicBlockTileEntity;
import com.yuanno.block_clover.data.entity.EntityStatsCapability;
import com.yuanno.block_clover.data.entity.IEntityStats;
import com.yuanno.block_clover.entities.BCentity;
import com.yuanno.block_clover.init.ModBlocks;
import com.yuanno.block_clover.networking.PacketHandler;
import com.yuanno.block_clover.networking.server.SUpdateEquippedAbilityPacket;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AntiMagicManaZoneAbility extends ContinuousAbility implements IParallelContinuousAbility {
    public static final AbilityCore INSTANCE = new AbilityCore.Builder("Anti magic mana zone", AbilityCategories.AbilityCategory.ATTRIBUTE, AntiMagicManaZoneAbility.class)
            .setDescription("Creates a dome of anti magic, disabling everything mana related in the dome.")
            .setDamageKind(AbilityDamageKind.GUARD)
            .build();

    public static final int MAX_ZONE_SIZE = 30;
    public static final int MAX_THRESHOLD = 2;

    private List<BlockPos> blockList = new ArrayList<>();
    private List<BlockPos> placedBlockList = new ArrayList<>();
    private int roomSize = 0;
    private int chargingTicks = 0;
    private BlockPos centerBlock;

    boolean zoneSet = false;
    public AntiMagicManaZoneAbility()
    {
        super(INSTANCE);
        this.setmanaCost(0);
        this.setMaxCooldown(180);

        this.onStartContinuityEvent = this::onStartContinuityEvent;
        this.duringContinuityEvent = this::duringContinuityEvent;
        this.onEndContinuityEvent = this::onEndContinuityEvent;
        this.onStopContinuityEvent = this::onStopContinuityEvent;
    }
    public int getROOMSize()
    {
        return this.roomSize;
    }
    private boolean onStartContinuityEvent(PlayerEntity player)
    {
        this.chargingTicks = 10;
        this.setThreshold(MAX_THRESHOLD);
        return true;
    }

    public void duringContinuityEvent(PlayerEntity player, int timer)
    {
        if (timer == 180 * 20)
            this.stopContinuity(player);
        if (this.getThreshold() == 0)
        {
            if (this.blockList.isEmpty())
            {
                this.blockList.addAll(AbilityHelper.createSphere(player.level, player.blockPosition(), this.roomSize, true, ModBlocks.ANTIMAGIC.get(), 0));
                this.centerBlock = new BlockPos(player.getX(), player.getY(), player.getZ());
                player.level.setBlockAndUpdate(this.centerBlock, ModBlocks.ANTIMAGIC.get().defaultBlockState());
                TileEntity tileEntity = player.level.getBlockEntity(this.centerBlock);
                zoneSet = true;
                if (tileEntity != null && tileEntity instanceof AntiMagicBlockTileEntity)
                {
                    ((AntiMagicBlockTileEntity)tileEntity).setOwner(player);
                    ((AntiMagicBlockTileEntity)tileEntity).setChanged();
                }
                this.blockList.add(new BlockPos(MathHelper.floor(player.getX()), MathHelper.floor(player.getY()), MathHelper.floor(player.getZ())));
                this.placedBlockList.addAll(this.blockList);
                this.setThreshold(0);
            }
            else
            {
                int placedBlock = 0;
                Iterator<BlockPos> iterator = this.placedBlockList.iterator();
                while (iterator.hasNext())
                {
                    BlockPos pos = iterator.next();
                    player.level.sendBlockUpdated(pos, Blocks.AIR.defaultBlockState(), ModBlocks.ANTIMAGIC.get().defaultBlockState(), 0);
                    iterator.remove();
                    placedBlock++;
                    if (placedBlock > 500)
                        return;
                }
            }
        }
        else
        {
            if(0 >= this.chargingTicks)
            {
                this.chargingTicks = 19;
            }
            else
                this.chargingTicks++;
        }
        if (zoneSet)
        {
            if (!isEntityInThisRoom(player))
                this.stopContinuity(player);
            List<Entity> entities = Beapi.getEntitiesAround(this.centerBlock, player.level, (float) 1 * roomSize / 2.0f);
            if (entities.contains(player)) {
                entities.forEach(entity -> {
                    if (entity instanceof BCentity) {
                        BCentity bCentity = (BCentity) entity;
                        bCentity.canUseMagic = false;
                    } else if (entity instanceof PlayerEntity) {
                        PlayerEntity playerEntity = (PlayerEntity) entity;
                        IEntityStats entityStats = EntityStatsCapability.get(playerEntity);
                        entityStats.setMana(0);
                    }
                });
            }
        }
    }

    private boolean onEndContinuityEvent(PlayerEntity player)
    {
        if (this.getThreshold() > 0)
        {
            this.setThreshold(0);
            PacketHandler.sendToAllTrackingAndSelf(new SUpdateEquippedAbilityPacket(player, this), player);
            this.roomSize = Math.max(8, (int) (MAX_ZONE_SIZE * ((this.continueTime / 20.0 ) / MAX_THRESHOLD)));
            return false;
        }
        return true;
    }

    private boolean onStopContinuityEvent(PlayerEntity player)
    {
        for (BlockPos pos : this.blockList)
        {
            Block currentBlock = player.level.getBlockState(pos).getBlock();
            if (currentBlock == ModBlocks.ANTIMAGIC.get() || currentBlock == ModBlocks.ANTIMAGIC.get())
                player.level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        }
//        player.level.setBlockAndUpdate(centerBlock, Blocks.AIR.defaultBlockState());
        this.zoneSet = false;
        this.blockList.clear();
        this.placedBlockList.clear();
        this.setMaxCooldown(0);
        PacketHandler.sendToAllTrackingAndSelf(new SUpdateEquippedAbilityPacket(player, this), player);
        return true;
    }

    public boolean isEntityInThisRoom(Entity entity)
    {
        return this.isInsideROOM(entity.level, entity.blockPosition());
    }

    public boolean isInsideROOM(World world, BlockPos pos)
    {
        int roomSize = this.roomSize;
        for (int i = -roomSize; i < roomSize; i++)
            for (int j = -roomSize; j < roomSize; j++)
                for (int k = -roomSize; k < roomSize; k++)
                {
                    BlockPos posCheck = pos.offset(i, j, k);
                    if (world.getBlockState(posCheck).getBlock() == ModBlocks.ANTIMAGIC.get())
                    {
                        double distance = pos.distSqr(posCheck);
                        if(distance < (roomSize - 1) * (roomSize - 1))
                            return true;
                    }
                }

        return false;
    }
}
