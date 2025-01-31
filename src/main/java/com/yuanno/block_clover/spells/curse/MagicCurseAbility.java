package com.yuanno.block_clover.spells.curse;

import com.yuanno.block_clover.api.ability.AbilityCategories;
import com.yuanno.block_clover.api.ability.AbilityCore;
import com.yuanno.block_clover.api.ability.AbilityDamageKind;
import com.yuanno.block_clover.api.ability.sorts.PunchAbility;
import com.yuanno.block_clover.init.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;

public class MagicCurseAbility extends PunchAbility {
    public static final AbilityCore INSTANCE = new AbilityCore.Builder("Magic curse", AbilityCategories.AbilityCategory.DEVIL, MagicCurseAbility.class)
            .setDescription("Completely disables the magic of the target for up to a minute, binding the entity if it's not a player")
            .setDamageKind(AbilityDamageKind.CURSE)
            .build();

    public MagicCurseAbility()
    {
        super(INSTANCE);
        this.setMaxCooldown(360);
        this.setmanaCost(0);
        this.setExperiencePoint(0);
        this.onHitEntityEvent = this::onHitEntityEvent;
    }

    private float onHitEntityEvent(PlayerEntity player, LivingEntity target)
    {
        target.addEffect(new EffectInstance(ModEffects.MAGIC_CURSE.get(), 1200, 0));
        return 0;
    }
}
