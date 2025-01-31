package com.yuanno.block_clover.entities.humanoid;

import com.yuanno.block_clover.client.IDynamicRenderer;
import com.yuanno.block_clover.entities.BCentity;
import com.yuanno.block_clover.events.GuildEvents;
import com.yuanno.block_clover.init.ModAttributes;
import com.yuanno.block_clover.networking.PacketHandler;
import com.yuanno.block_clover.networking.server.SOpenGuildScreenpacket;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class GuildEntity extends BCentity implements IDynamicRenderer {

    public GuildEntity(EntityType type, World world)
    {
        super(type, world);
    }
    @Override
    protected void registerGoals()
    {
        super.registerGoals();
        this.goalSelector.addGoal(0, new MeleeAttackGoal(this, 1, true));
        this.goalSelector.addGoal(2, new LookAtGoal(this, PlayerEntity.class, 4));
        this.goalSelector.addGoal(3, new LookRandomlyGoal(this));

        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
    }

    public static AttributeModifierMap.MutableAttribute setCustomAttributes()
    {
        return MobEntity.createMobAttributes()
                .add(Attributes.ATTACK_DAMAGE, 50)
                .add(Attributes.ARMOR, 20)
                .add(Attributes.MAX_HEALTH, 300)
                .add(Attributes.FOLLOW_RANGE, 100)
                .add(Attributes.MOVEMENT_SPEED, 3)
                .add(ModAttributes.FALL_RESISTANCE.get(), 50);

    }
    @Override
    public ActionResultType mobInteract(PlayerEntity player, Hand hand)
    {
        if (hand != Hand.MAIN_HAND)
            return ActionResultType.PASS;
        if (!player.level.isClientSide)
        {
            this.lookAt(player, 1, 1);
            PacketHandler.sendTo(new SOpenGuildScreenpacket(this.getId()), player);
        }
        return ActionResultType.PASS;
    }

    @Override
    public String getMobTexture() {
        return "grimoire_magician1"; // Todo work on this
    }

    @Override
    public String getDefaultTexture() {
        return null;
    }
}
