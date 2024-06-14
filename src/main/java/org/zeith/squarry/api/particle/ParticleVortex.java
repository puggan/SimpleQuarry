package org.zeith.squarry.api.particle;

import lombok.Getter;
import net.minecraft.world.phys.AABB;

@Getter
public class ParticleVortex
{
	protected double x;
	protected double y;
	protected double z;
	protected double vortexStrength;
	protected double radius = 1.0;
	protected AABB boundingBox;

	public ParticleVortex(double x, double y, double z, double vortexStrength, double radius, AABB boundingBox)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.vortexStrength = vortexStrength;
		this.radius = radius;
		this.boundingBox = boundingBox == null ? rebuildBoundingBox() : boundingBox;
	}

	public void update()
	{
	}

	public AABB rebuildBoundingBox()
	{
		this.boundingBox = new AABB(this.x - this.radius, this.y - this.radius, this.z - this.radius, this.x + this.radius, this.y + this.radius, this.z + this.radius);
		return this.boundingBox;
	}

	public AABB getBoundingBox()
	{
		if(this.boundingBox == null
		   || this.boundingBox.maxX - this.boundingBox.minX != this.radius * 2.0
		   || this.boundingBox.maxX - this.radius != this.x
		   || this.boundingBox.maxY - this.radius != this.y
		   || this.boundingBox.maxZ - this.radius != this.z)
			this.rebuildBoundingBox();
		return this.boundingBox;
	}
}