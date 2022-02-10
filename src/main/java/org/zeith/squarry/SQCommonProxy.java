package org.zeith.squarry;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.zeith.squarry.api.particle.ParticleVortex;
import org.zeith.squarry.blocks.entity.TileFuelQuarry;

public class SQCommonProxy
{
	public static final ItemStack COAL = new ItemStack(Items.COAL);

	public ParticleVortex createQuarryVortex(TileFuelQuarry quarry)
	{
		return null;
	}
}
