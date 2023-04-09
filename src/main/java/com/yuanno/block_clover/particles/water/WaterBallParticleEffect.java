package com.yuanno.block_clover.particles.water;

import com.yuanno.block_clover.api.Beapi;
import com.yuanno.block_clover.init.ModParticleTypes;
import com.yuanno.block_clover.particles.GenericParticleData;
import com.yuanno.block_clover.particles.ParticleEffect;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class WaterBallParticleEffect extends ParticleEffect {

    int numParticles = 5;
    double particleSpread = 0.2;
    double offset = 1.5;

    @Override
    public void spawn(World world, double posX, double posY, double posZ, double motionX, double motionY, double motionZ) {
        for (int i = 0; i < numParticles; i++) {
            double offsetX = (Math.random() - 0.5) * particleSpread;
            double offsetY = (Math.random() - 0.5) * particleSpread;
            double offsetZ = (Math.random() - 0.5) * particleSpread;
            double particlePosX = posX - offset * motionX;
            double particlePosY = posY - offset * motionY;
            double particlePosZ = posZ - offset * motionZ;
            GenericParticleData waterParticle = new GenericParticleData(ModParticleTypes.WATER.get());
            waterParticle.setLife(10);
            waterParticle.setSize(0.5f);
            waterParticle.setMotion(0, 0.01, 0);
            Beapi.spawnParticles(waterParticle, (ServerWorld) world, particlePosX + offsetX, particlePosY + offsetY, particlePosZ + offsetZ);
        }
    }
}
