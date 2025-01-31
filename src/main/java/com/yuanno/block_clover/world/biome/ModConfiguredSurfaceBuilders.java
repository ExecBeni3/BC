package com.yuanno.block_clover.world.biome;

import com.yuanno.block_clover.Main;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.ISurfaceBuilderConfig;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

public class ModConfiguredSurfaceBuilders {

    public static ConfiguredSurfaceBuilder<?> GRAND_MAGIC_ZONE_VOLCANO = register("volcano_zone", SurfaceBuilder.NETHER_FOREST.configured(new SurfaceBuilderConfig(
            Blocks.BASALT.defaultBlockState(), Blocks.MAGMA_BLOCK.defaultBlockState(), Blocks.NETHERRACK.defaultBlockState()
    )));
    public static ConfiguredSurfaceBuilder<?> MOGURO_FOREST = register("moguro_forest", SurfaceBuilder.DEFAULT.configured(new SurfaceBuilderConfig(
            Blocks.GRASS_BLOCK.defaultBlockState(), Blocks.MYCELIUM.defaultBlockState(), Blocks.GRASS_BLOCK.defaultBlockState()
    )));

    public static <SC extends ISurfaceBuilderConfig>ConfiguredSurfaceBuilder<SC> register(String name, ConfiguredSurfaceBuilder<SC> csb)
    {
        return WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_SURFACE_BUILDER, new ResourceLocation(Main.MODID, name), csb);
    }
}
