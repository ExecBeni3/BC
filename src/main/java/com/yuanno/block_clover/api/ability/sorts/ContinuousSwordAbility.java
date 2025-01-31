package com.yuanno.block_clover.api.ability.sorts;

import com.yuanno.block_clover.api.ability.AbilityCategories;
import com.yuanno.block_clover.api.ability.AbilityCore;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.io.Serializable;

public abstract class ContinuousSwordAbility extends ContinuousAbility {

    // Setting the defaults so that no crash occurs and so they will be null safe.
    protected IOnHitEntity onHitEntityEvent = (player, target) -> { return 0; };

    public ContinuousSwordAbility(AbilityCore core)
    {
        super(core);
    }


    /*
     *  Methods
     */
    public float hitEntity(PlayerEntity player, LivingEntity target)
    {
        float result = this.onHitEntityEvent.onHitEntity(player, target);


        //this.stopContinuity(player);
        return result;
    }

    /*
     *	Interfaces
     */
    public interface IOnHitEntity extends Serializable
    {
        float onHitEntity(PlayerEntity player, LivingEntity target);
    }
}
