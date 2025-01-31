package com.yuanno.block_clover.entities;

import com.yuanno.block_clover.Main;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.system.CallbackI;

@Mod.EventBusSubscriber(modid = Main.MODID)
public class WaterDamageEvent {

    @SubscribeEvent
    public static void inWaterDamage(LivingHurtEvent event)
    {
        if (!event.getEntityLiving().level.isClientSide)
        {
            if (event.getEntityLiving() instanceof BCWaterEntity || event.getEntityLiving() instanceof BCentity) {
                if (event.getEntityLiving().isInWater()) {
                    DamageSource damageSource = event.getSource();
                    if (damageSource == DamageSource.DROWN)
                        event.setCanceled(true);
                }
            }
        }
    }
}
