package com.yuanno.block_clover.events.ability;

import com.yuanno.block_clover.Main;
import com.yuanno.block_clover.events.projectiles.ProjectileBlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Main.MODID)
public class AbilityProjectileEvents
{
	@SubscribeEvent
	public static void onBlockCheck(ProjectileBlockEvent event)
	{
		if( false )
		{
			event.setCanBlock(true);
		}
	}
}
