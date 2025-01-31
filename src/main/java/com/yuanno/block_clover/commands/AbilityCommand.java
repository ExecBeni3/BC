package com.yuanno.block_clover.commands;

import com.yuanno.block_clover.api.Beapi;
import com.yuanno.block_clover.api.BeoDebug;
import com.yuanno.block_clover.data.ability.AbilityDataCapability;
import com.yuanno.block_clover.data.ability.IAbilityData;
import com.yuanno.block_clover.networking.PacketHandler;
import com.yuanno.block_clover.networking.server.SSyncAbilityDataPacket;
import com.yuanno.block_clover.networking.server.SUpdateEquippedAbilityPacket;
import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.yuanno.block_clover.api.ability.*;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import java.util.Collection;

public class AbilityCommand
{
	public static void register(CommandDispatcher<CommandSource> dispatcher)
	{
		LiteralArgumentBuilder<CommandSource> builder = Commands.literal("ability").requires(source -> source.hasPermission(3));

		builder
			.then(Commands.literal("give")
				.then(Commands.argument("ability", AbilityArgument.ability())
					.then(Commands.argument("targets", EntityArgument.players())
						.executes(context -> addAbility(context, AbilityArgument.getAbility(context, "ability"), EntityArgument.getPlayers(context, "targets")))
					)
					.executes(context -> addAbility(context, AbilityArgument.getAbility(context, "ability"), getDefaultCollection(context)))
				)
			)
			.then(Commands.literal("remove")
				.then(Commands.argument("ability", new AbilityArgument())
					.then(Commands.argument("targets", EntityArgument.players())
						.executes(context -> removeAbility(context, AbilityArgument.getAbility(context, "ability"), EntityArgument.getPlayers(context, "targets")))
					)
					.executes(context -> removeAbility(context, AbilityArgument.getAbility(context, "ability"), getDefaultCollection(context)))
				)
			)
			.then(Commands.literal("unlock_group")
				.then(Commands.argument("group", AbilityGroupArgument.abilityGroup())
				.then(Commands.argument("targets", EntityArgument.players())
					.executes(context -> abilityGroup(context, context.getArgument("group", AbilityCommandGroup.class), 1, EntityArgument.getPlayers(context, "targets"))))))
			.then(Commands.literal("lock_group")
				.then(Commands.argument("group", AbilityGroupArgument.abilityGroup())
				.then(Commands.argument("targets", EntityArgument.players())
					.executes(context -> abilityGroup(context, context.getArgument("group", AbilityCommandGroup.class), -1, EntityArgument.getPlayers(context, "targets"))))))
			.then(Commands.literal("reset_cooldown")
				.then(Commands.argument("targets", EntityArgument.players())
					.executes(context -> resetCooldown(context, EntityArgument.getPlayers(context, "targets")))
				)
				.executes(context -> resetCooldown(context, getDefaultCollection(context)))
			);
		
		dispatcher.register(builder);
	}

	private static int abilityGroup(CommandContext<CommandSource> context, AbilityCommandGroup group, int op, Collection<ServerPlayerEntity> players)
	{
		for (ServerPlayerEntity player : players)
		{
			IAbilityData abilityProps = AbilityDataCapability.get(player);

			for(AbilityCore core : group.getAbilities())
			{
				if(op == 1)
				{
					Ability abl = core.createAbility();
					abl.setUnlockType(AbilityUnlock.COMMAND);
					abilityProps.addUnlockedAbility(player, abl);
				}
				else
					abilityProps.removeUnlockedAbility(core);
			}

			PacketHandler.sendTo(new SSyncAbilityDataPacket(player.getId(), abilityProps), player);
		}
		return 1;
	}

	private static Collection<ServerPlayerEntity> getDefaultCollection(CommandContext<CommandSource> context) throws CommandSyntaxException
	{
		return Lists.newArrayList(context.getSource().getPlayerOrException());
	}


	private static int resetCooldown(CommandContext<CommandSource> context, Collection<ServerPlayerEntity> players)
	{
		for (ServerPlayerEntity player : players)
		{
			IAbilityData props = AbilityDataCapability.get(player);

			for(Ability ability : props.getEquippedAbilities())
			{
				if(ability == null)
					continue;
				
				ability.setForcedState(false);
				
				if(ability.isOnCooldown())
					ability.stopCooldown(player);
				

				PacketHandler.sendToAllTrackingAndSelf(new SUpdateEquippedAbilityPacket(player, ability), player);
			}
		}
		
		return 1;
	}

	private static int addAbility(CommandContext<CommandSource> context, AbilityCore core, Collection<ServerPlayerEntity> targets)
	{
		for (ServerPlayerEntity player : targets)
		{
			IAbilityData props = AbilityDataCapability.get(player);
			Ability ability = core.createAbility();

			ability.setUnlockType(AbilityUnlock.COMMAND);
			props.addUnlockedAbility(player, ability);
			
			if(BeoDebug.isDebug())
				player.sendMessage(new StringTextComponent(TextFormatting.GREEN + "" + TextFormatting.ITALIC + "[DEBUG] " + ability.getName() + " unlocked for " + player.getName().getString()), Util.NIL_UUID);

			PacketHandler.sendTo(new SSyncAbilityDataPacket(player.getId(), props), player);
		}
		
		return 1;
	}

	private static int removeAbility(CommandContext<CommandSource> context, AbilityCore ability, Collection<ServerPlayerEntity> targets)
	{
		for (ServerPlayerEntity player : targets)
		{
			IAbilityData props = AbilityDataCapability.get(player);

			props.removeUnlockedAbility(ability);

			if(Beapi.isDebug())
				player.sendMessage(new StringTextComponent(TextFormatting.GREEN + "" + TextFormatting.ITALIC + "[DEBUG] " + ability.getName() + " removed for " + player.getName().getString()), Util.NIL_UUID);

			PacketHandler.sendTo(new SSyncAbilityDataPacket(player.getId(), props), player);
		}

		return 1;
	}
}
