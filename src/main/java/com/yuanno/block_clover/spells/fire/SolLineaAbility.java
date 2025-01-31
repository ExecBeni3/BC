package com.yuanno.block_clover.spells.fire;

import com.yuanno.block_clover.api.ability.Ability;
import com.yuanno.block_clover.api.ability.AbilityCategories;
import com.yuanno.block_clover.api.ability.AbilityCore;
import com.yuanno.block_clover.api.ability.AbilityDamageKind;
import com.yuanno.block_clover.entities.projectiles.fire.SolLineaProjectile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.play.server.SAnimateHandPacket;
import net.minecraft.world.server.ServerWorld;

public class SolLineaAbility extends Ability {
    public static final AbilityCore INSTANCE = new AbilityCore.Builder("Sol linea", AbilityCategories.AbilityCategory.ATTRIBUTE, SolLineaAbility.class)
            .setDescription("Shoots a concentrated amount of fire at one point")
            .setDamageKind(AbilityDamageKind.ELEMENTAL)
            .build();

    public SolLineaAbility()
    {
        super(INSTANCE);
        this.setMaxCooldown(20);
        this.setmanaCost(30);
        this.setEvolutionCost(30);
        this.setExperiencePoint(30);
        this.onUseEvent = this::onUseEvent;
    }

    private boolean onUseEvent(PlayerEntity player)
    {
        SolLineaProjectile projectile = new SolLineaProjectile(player.level, player);
        if (this.isEvolved()) {
            projectile.setDamage(20);
            projectile.setArmorPiercing();
            projectile.setMaxLife(164);
            projectile.setPassThroughEntities();
        }
        player.level.addFreshEntity(projectile);
        ((ServerWorld) player.level).getChunkSource().broadcastAndSend(player, new SAnimateHandPacket(player, 0));
        projectile.shootFromRotation(player, player.xRot, player.yRot, 0, 5f, 1);

        return true;
    }
}
