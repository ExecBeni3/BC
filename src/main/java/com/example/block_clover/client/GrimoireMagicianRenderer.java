package com.example.block_clover.client;

import com.example.block_clover.Main;
import com.example.block_clover.entities.GrimoireMagicianEntity;
import com.example.block_clover.models.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.IRenderFactory;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("unchecked")
public class GrimoireMagicianRenderer extends MobRenderer<GrimoireMagicianEntity, HumanoidModel<GrimoireMagicianEntity>> {

    protected static final ResourceLocation TEXTURE =
            new ResourceLocation(Main.MODID, "textures/entities/npc/grimoire_magician1.png");

    public GrimoireMagicianRenderer(EntityRendererManager renderManager)
    {
        super(renderManager, new HumanoidModel<>(), 0.1F);
    }

    @Override
    public ResourceLocation getTextureLocation(GrimoireMagicianEntity p_110775_1_) {
        return TEXTURE;
    }

    public static class Factory implements IRenderFactory<GrimoireMagicianEntity> {

        @Override
        public EntityRenderer<? super GrimoireMagicianEntity> createRenderFor(EntityRendererManager manager) {
            return new GrimoireMagicianRenderer(manager);
        }
    }
}
