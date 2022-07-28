package com.example.block_clover.spells.antimagic;

import com.example.block_clover.api.ability.AbilityCategories;
import com.example.block_clover.api.ability.interfaces.IParallelContinuousAbility;
import com.example.block_clover.api.ability.sorts.ItemAbility;
import com.example.block_clover.init.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class DemonDwellerAbility extends ItemAbility implements IParallelContinuousAbility {

    public static final DemonDwellerAbility INSTANCE = new DemonDwellerAbility();

    public DemonDwellerAbility()
    {
        super("Demon Dweller", AbilityCategories.AbilityCategory.ATTRIBUTE);
        this.setDescription("Takes the demon dweller sword out of your grimoire");
        this.setMaxCooldown(0);
        this.setmanaCost(0);
    }

    @Override
    public ItemStack getItemStack(PlayerEntity player)
    {
        return new ItemStack(ModItems.DEMON_DWELLER.get());
    }

    @Override
    public boolean canBeActive(PlayerEntity player) {
        return true;
    }
}
