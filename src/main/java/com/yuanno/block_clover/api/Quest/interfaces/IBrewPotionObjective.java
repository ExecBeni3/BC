package com.yuanno.block_clover.api.Quest.interfaces;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface IBrewPotionObjective
{
	boolean checkPotion(PlayerEntity player, ItemStack stack);
}
