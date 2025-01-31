package com.yuanno.block_clover.entities.summons.mercury;

import com.yuanno.block_clover.api.Beapi;
import com.yuanno.block_clover.api.ability.AbilityProjectileEntity;
import com.yuanno.block_clover.entities.summons.water.WaterSummons;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.List;
import java.util.UUID;

public class MercuryBubbleEntity extends Entity {

    private LivingEntity owner;
    private UUID ownerUUID;
    private float manaIn;
    private final double speedLimit = 1;
    private final double pullSpeed = 0.7;
    //private static final ParticleEffect PARTICLES = new BlackHoleParticleEffect();

    public MercuryBubbleEntity(EntityType type, World world) {
        super(type, world);
    }

    public MercuryBubbleEntity(World world, LivingEntity owner)
    {
        super(MercurySummons.MERCURY_BUBBLE.get(), world);
        this.ownerUUID = owner.getUUID();
    }

    public void setOwner(UUID uuid)
    {
        this.ownerUUID = uuid;
        this.owner = (LivingEntity) ((ServerWorld) this.level).getEntity(this.ownerUUID);
    }

    @Override
    public boolean isColliding(BlockPos pPos, BlockState pState) {
        return super.isColliding(pPos, pState);
    }

    @Override
    public void tick()
    {
        super.tick();

        if(this.level.isClientSide)
            return;

        if (this.ownerUUID == null)
        {
            this.remove();
            return;
        }

        if(this.owner == null)
        {
            this.owner = (LivingEntity) ((ServerWorld) this.level).getEntity(this.ownerUUID);
            return;
        }

        this.yRot = (this.owner.getYHeadRot() - 90.0F) * -1;
        if(Math.round(this.getY()) > Math.round(owner.getY() + 3))
            this.move(MoverType.SELF, new Vector3d(0, -0.1, 0));
        else if (Math.round(this.getY()) < Math.round(owner.getY() + 3))
            this.move(MoverType.SELF, new Vector3d(0, 0.1, 0));


        double posX = this.getX() - this.owner.getX();
        double posZ = this.getZ() - this.owner.getZ();
        double posY = this.getY() - this.owner.getY();

        if(posX > this.speedLimit)
            posX = this.speedLimit;
        else if(posX < 0 && posX < -this.speedLimit)
            posX = -this.speedLimit;

        if(posZ > this.speedLimit)
            posZ = this.speedLimit;
        else if(posZ < 0 && posZ < -this.speedLimit)
            posZ = -this.speedLimit;

        this.move(MoverType.SELF, new Vector3d(-posX, -posY + 1.2, -posZ));


        if(this.owner.getHealth() <= 0)
            this.remove();
        if (this.tickCount > 120)
            this.remove();

        List<AbilityProjectileEntity> spellProjectiles = Beapi.getEntitiesNear(this.getOnPos(), this.level, 16, AbilityProjectileEntity.class);



        spellProjectiles.forEach(spellProjectile -> {

            if(spellProjectile.getOwner() == this.owner)
                return;

            double spellPosX = spellProjectile.getX() - this.getX();
            double spellPosZ = spellProjectile.getZ() - this.getZ();
            double spellPosY = spellProjectile.getZ() - this.getZ();

            if(Math.round(spellProjectile.getY()) > Math.round(this.getY()))
                spellProjectile.move(MoverType.SELF, new Vector3d(0, -0.7, 0));
            else if (Math.round(spellProjectile.getY()) < Math.round(this.getY()))
                spellProjectile.move(MoverType.SELF, new Vector3d(0, 0.7, 0));

            if(spellPosX > this.pullSpeed)
                spellPosX = this.pullSpeed;
            else if(spellPosX < 0 && spellPosX < -this.pullSpeed)
                spellPosX = -this.pullSpeed;

            if(spellPosZ > this.pullSpeed)
                spellPosZ = this.pullSpeed;
            else if(spellPosZ < 0 && spellPosZ < -this.pullSpeed)
                spellPosZ = -this.pullSpeed;

            spellProjectile.move(MoverType.SELF, new Vector3d(-spellPosX, -spellPosY, -spellPosZ));

            boolean xMatches = Math.round(spellProjectile.getX()) == Math.round(this.getX());
            boolean yMatches = Math.round(spellProjectile.getY()) == Math.round(this.getY());
            boolean zMatches = Math.round(spellProjectile.getZ()) == Math.round(this.getZ());

            if(xMatches && yMatches && zMatches){
                spellProjectile.remove();
            }

        });

    }

    @Override
    protected void defineSynchedData() {}

    @Override
    protected void addAdditionalSaveData(CompoundNBT compound)
    {
        compound.putString("OwnerUUID", this.ownerUUID.toString());
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT compound)
    {
        if (compound.contains("OwnerUUID", 8))
            this.setOwner(UUID.fromString(compound.getString("OwnerUUID")));
    }

    @Override
    public IPacket<?> getAddEntityPacket()
    {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public void setRot(float yDirection, float xDirection) {
        this.yRot = yDirection % 360.0F;
        this.xRot = xDirection % 360.0F;

    }
}
