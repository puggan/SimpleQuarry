package org.zeith.squarry.mixins.client;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.Queue;

@Mixin(ParticleEngine.class)
@OnlyIn(Dist.CLIENT)
public interface ParticleEngineAccessor
{
	@Accessor
	Map<ParticleRenderType, Queue<Particle>> getParticles();
}
