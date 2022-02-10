package org.zeith.squarry.api.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import org.zeith.squarry.blocks.entity.TileFuelQuarry;

public class ClientQuarryVortex
		extends ClientVortex
{
	public final TileFuelQuarry quarry;

	public ClientQuarryVortex(TileFuelQuarry quarry)
	{
		super(quarry.getBlockPos().getX() + 0.5, quarry.getBlockPos().getY() + 0.5, quarry.getBlockPos().getZ() + 0.5, 16.0, 1.0, null);
		this.quarry = quarry;
	}

	AABB below;

	@Override
	public void update()
	{
		below = new AABB(quarry.getBlockPos().below()).move(0, 0.8, 0);
		x = quarry.getBlockPos().getX() + 0.5;
		y = quarry.getBlockPos().getY();
		z = quarry.getBlockPos().getZ() + 0.5;
		super.update();
	}

	@Override
	protected void processParticle(Particle p)
	{
		super.processParticle(p);
		if(p.getBoundingBox().intersects(below))
		{
			p.friction = 0;
			double px = p.x;
			double py = p.y;
			double pz = p.z;
			double mx = (Mth.clamp((this.x - px), -1.0, 1.0) / 4);
			double my = (Mth.clamp((this.y - py - 0.55), -1.0, 1.0) / 32);
			double mz = (Mth.clamp((this.z - pz), -1.0, 1.0) / 4);
			p.xd = mx;
			p.yd = my;
			p.zd = mz;
			p.age = Math.min(p.age + 8, p.getLifetime() - 1);
		}
	}

	@Override
	public AABB getBoundingBox()
	{
		return quarry.boundingBox;
	}
}