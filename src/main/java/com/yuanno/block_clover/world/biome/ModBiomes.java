package com.yuanno.block_clover.world.biome;

import com.yuanno.block_clover.Main;
import com.yuanno.block_clover.init.ModEntities;
import net.minecraft.client.audio.BackgroundMusicTracks;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.*;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Features;
import net.minecraft.world.gen.feature.structure.StructureFeatures;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilders;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class ModBiomes {

    public static final DeferredRegister<Biome> BIOMES = DeferredRegister.create(ForgeRegistries.BIOMES, Main.MODID);

    public static final RegistryObject<Biome> GRAND_MAGIC_ZONE_VOLCANO = BIOMES.register("volcano_zone", () -> makeVolcanoBiome(() -> ModConfiguredSurfaceBuilders.GRAND_MAGIC_ZONE_VOLCANO, 0.18f, 0.15f));
    public static final RegistryObject<Biome> MOGURO_FOREST = BIOMES.register("moguro_forest", () -> makeMoguroBiome(() -> ModConfiguredSurfaceBuilders.MOGURO_FOREST, 0.18f, 0.15f));

    public static Biome makeMoguroBiome(final Supplier<ConfiguredSurfaceBuilder<?>> surfaceBuilder, float depth, float scale) {
        MobSpawnInfo.Builder lvt_3_1_ = new MobSpawnInfo.Builder();
        DefaultBiomeFeatures.farmAnimals(lvt_3_1_);
        DefaultBiomeFeatures.commonSpawns(lvt_3_1_);
        BiomeGenerationSettings.Builder lvt_4_1_ = (new BiomeGenerationSettings.Builder()).surfaceBuilder(ConfiguredSurfaceBuilders.GRASS);
        DefaultBiomeFeatures.addDefaultOverworldLandStructures(lvt_4_1_);
        lvt_4_1_.addStructureStart(StructureFeatures.RUINED_PORTAL_STANDARD);
        DefaultBiomeFeatures.addDefaultCarvers(lvt_4_1_);
        DefaultBiomeFeatures.addDefaultLakes(lvt_4_1_);
        DefaultBiomeFeatures.addDefaultMonsterRoom(lvt_4_1_);
        DefaultBiomeFeatures.addForestFlowers(lvt_4_1_);
        DefaultBiomeFeatures.addDefaultUndergroundVariety(lvt_4_1_);
        DefaultBiomeFeatures.addDefaultOres(lvt_4_1_);
        DefaultBiomeFeatures.addDefaultSoftDisks(lvt_4_1_);


        DefaultBiomeFeatures.addDefaultFlowers(lvt_4_1_);
        DefaultBiomeFeatures.addForestGrass(lvt_4_1_);
        DefaultBiomeFeatures.addDefaultMushrooms(lvt_4_1_);
        DefaultBiomeFeatures.addDefaultExtraVegetation(lvt_4_1_);
        DefaultBiomeFeatures.addDefaultSprings(lvt_4_1_);
        DefaultBiomeFeatures.addSurfaceFreezing(lvt_4_1_);
        return (new Biome.Builder()).precipitation(Biome.RainType.RAIN).biomeCategory(Biome.Category.FOREST)
                .depth(depth)
                .scale(scale)
                .temperature(0.6F)
                .downfall(0.6F)
                .specialEffects((new BiomeAmbience.Builder())
                        .waterColor(4159204)
                        .waterFogColor(329011)
                        .fogColor(12638463)
                        .skyColor(calculateSkyColor())
                        .ambientMoodSound(MoodSoundAmbience.LEGACY_CAVE_SETTINGS)
                        .build()).mobSpawnSettings(lvt_3_1_.build()).generationSettings(lvt_4_1_.build()).build();
    }
    private static int calculateSkyColor() {
        float lvt_1_1_ = (float) 0.6 / 3.0F;
        lvt_1_1_ = MathHelper.clamp(lvt_1_1_, -1.0F, 1.0F);
        return MathHelper.hsvToRgb(0.62222224F - lvt_1_1_ * 0.05F, 0.5F + lvt_1_1_ * 0.1F, 1.0F);
    }

    private static Biome makeVolcanoBiome(final Supplier<ConfiguredSurfaceBuilder<?>> surfaceBuilder, float depth, float scale) {
        MobSpawnInfo.Builder mobspawninfo$builder = new MobSpawnInfo.Builder();
        DefaultBiomeFeatures.farmAnimals(mobspawninfo$builder);
        DefaultBiomeFeatures.commonSpawns(mobspawninfo$builder);
        mobspawninfo$builder.addSpawn(EntityClassification.MONSTER,
                new MobSpawnInfo.Spawners(EntityType.BLAZE, 45, 7, 10));

        //TODO make it generate
        mobspawninfo$builder.addSpawn(EntityClassification.CREATURE,
                new MobSpawnInfo.Spawners(ModEntities.VOLCANO_MONSTER.get(), 200, 1, 2));




        BiomeGenerationSettings.Builder biomegenerationsettings$builder =
                (new BiomeGenerationSettings.Builder()).surfaceBuilder(surfaceBuilder);

        biomegenerationsettings$builder.addStructureStart(StructureFeatures.RUINED_PORTAL_SWAMP);

        DefaultBiomeFeatures.addDefaultCarvers(biomegenerationsettings$builder);

        DefaultBiomeFeatures.addDefaultMonsterRoom(biomegenerationsettings$builder);
        DefaultBiomeFeatures.addDefaultUndergroundVariety(biomegenerationsettings$builder);
        DefaultBiomeFeatures.addDefaultOres(biomegenerationsettings$builder);
        DefaultBiomeFeatures.addSwampClayDisk(biomegenerationsettings$builder);
        DefaultBiomeFeatures.addDefaultMushrooms(biomegenerationsettings$builder);
        DefaultBiomeFeatures.addDesertExtraVegetation(biomegenerationsettings$builder);

        biomegenerationsettings$builder.addFeature(GenerationStage.Decoration.LAKES, Features.LAKE_LAVA);
        DefaultBiomeFeatures.addSurfaceFreezing(biomegenerationsettings$builder);

        return (new Biome.Builder()).precipitation(Biome.RainType.NONE).biomeCategory(Biome.Category.DESERT).depth(depth).scale(scale)
                .temperature(1.5F).downfall(0.9F).specialEffects((new BiomeAmbience.Builder()).waterColor(-3407872).waterFogColor(-16777216)
                        .fogColor(-65536).skyColor(getSkyColorWithTemperatureModifier(0.8F)).foliageColorOverride(-3407872).grassColorOverride(-3407872)
                        .ambientParticle(new ParticleEffectAmbience(ParticleTypes.LAVA, 0.003f)).skyColor(-65536)
                        .ambientLoopSound(SoundEvents.AMBIENT_CRIMSON_FOREST_LOOP)
                        .ambientMoodSound(new MoodSoundAmbience(SoundEvents.AMBIENT_WARPED_FOREST_MOOD, 6000, 8, 2.0D))
                        .ambientAdditionsSound(new SoundAdditionsAmbience(SoundEvents.AMBIENT_NETHER_WASTES_ADDITIONS, 0.0111D))
                        .backgroundMusic(BackgroundMusicTracks.createGameMusic(SoundEvents.AMBIENT_NETHER_WASTES_LOOP))
                        .build())
                .mobSpawnSettings(mobspawninfo$builder.build()).generationSettings(biomegenerationsettings$builder.build()).build();
    }

    private static int getSkyColorWithTemperatureModifier(float temperature) {
        float lvt_1_1_ = temperature / 3.0F;
        lvt_1_1_ = MathHelper.clamp(lvt_1_1_, -1.0F, 1.0F);
        return MathHelper.hsvToRgb(0.2460909F - lvt_1_1_ * 0.05F, 0.5F + lvt_1_1_ * 0.1F, 1.0F);
    }

    public static void register(IEventBus eventBus)
    {
        BIOMES.register(eventBus);
    }
}
