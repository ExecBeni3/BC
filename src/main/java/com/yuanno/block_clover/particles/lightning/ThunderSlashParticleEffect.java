package com.yuanno.block_clover.particles.lightning;

import com.yuanno.block_clover.api.Beapi;
import com.yuanno.block_clover.init.ModParticleTypes;
import com.yuanno.block_clover.particles.GenericParticleData;
import com.yuanno.block_clover.particles.ParticleEffect;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ThunderSlashParticleEffect extends ParticleEffect {
    private static final int NUM_PARTICLES = 10;
    private static final double PARTICLE_SPREAD = 0.1;
    private static final double OFFSET = 0.5;
    private static final float SIZE = 0.05f;

    @Override
    public void spawn(World world, double posX, double posY, double posZ, double motionX, double motionY, double motionZ) {
        for (int i = 0; i < NUM_PARTICLES; i++) {
            double offsetX = (Math.random() - 0.5) * PARTICLE_SPREAD;
            double offsetY = (Math.random() - 0.5) * PARTICLE_SPREAD;
            double offsetZ = (Math.random() - 0.5) * PARTICLE_SPREAD;
            double particlePosX = posX + offsetX;
            double particlePosY = posY + 1.5;
            double particlePosZ = posZ + offsetZ;

            GenericParticleData electricParticle = new GenericParticleData(ModParticleTypes.LIGHTNING.get());
            electricParticle.setLife(10);
            electricParticle.setSize(SIZE);
            electricParticle.setMotion(motionX, motionY, motionZ);

            Beapi.spawnParticles(electricParticle, (ServerWorld) world, particlePosX, particlePosY, particlePosZ);
        }
    }


}
