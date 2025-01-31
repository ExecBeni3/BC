package com.yuanno.block_clover.effects;

import com.yuanno.block_clover.api.Beapi;
import com.yuanno.block_clover.data.entity.EntityStatsBase;
import com.yuanno.block_clover.data.entity.EntityStatsCapability;
import com.yuanno.block_clover.data.entity.IEntityStats;
import net.minecraft.client.gui.fonts.TexturedGlyph;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

import javax.annotation.Nullable;

public class XpMultiplierEffect extends Effect {
    private LivingEntity entity;
    public XpMultiplierEffect() {
        super(EffectType.BENEFICIAL, 3381504);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int p_76394_2_) {
        if(!entity.level.isClientSide()) {
            this.entity = entity;
            IEntityStats props = EntityStatsCapability.get(entity);
            props.alterManaRegeneration(0.2f);
        }
        super.applyEffectTick(entity, p_76394_2_);
    }


    @Override
    public boolean isDurationEffectTick(int duration, int p_76397_2_) {
        if(duration > 20) {
            return true;
        } else {
            if(entity != null) {
                IEntityStats props = EntityStatsCapability.get(entity);
                props.alterManaRegeneration(-0.2F);
            }
            return false;
        }
    }

}
