package org.zeith.squarry.api.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.zeith.squarry.mixins.client.ParticleEngineAccessor;

import java.util.Collection;

public class ClientVortex
		extends ParticleVortex
{
	public ClientVortex(double x, double y, double z, double vortexStrength, double radius, AABB boundingBox)
	{
		super(x, y, z, vortexStrength, radius, boundingBox);
	}

	@Override
	public void update()
	{
		AABB bb = getBoundingBox();
		((ParticleEngineAccessor) Minecraft.getInstance().particleEngine).getParticles()
				.values()
				.stream()
				.flatMap(Collection::stream)
				.filter(p -> p.getBoundingBox().intersects(bb))
				.forEach(this::processParticle);
	}

	protected void processParticle(Particle p)
	{
		p.friction = 1F;

		Vec3 dir = new Vec3(x - p.x, y - p.y, z - p.z)
				.normalize();

		p.xd = Mth.lerp(0.12, p.xd, dir.x);
		p.yd = Mth.lerp(0.12, p.yd, dir.y);
		p.zd = Mth.lerp(0.12, p.zd, dir.z);

		if(Math.random() > 0.4 && new Vec3(p.x - p.xo, p.y - p.yo, p.z - p.zo).length() > 0.1)
			p.age = Math.max(0, p.age - 1);
	}
}