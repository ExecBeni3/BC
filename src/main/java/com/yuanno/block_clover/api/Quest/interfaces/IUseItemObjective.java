package com.yuanno.block_clover.api.Quest.interfaces;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface IUseItemObjective
{
	boolean checkItem(PlayerEntity player, ItemStack itemStack, int duration);
}
