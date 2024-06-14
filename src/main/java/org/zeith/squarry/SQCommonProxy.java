package org.zeith.squarry;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import org.zeith.hammerlib.api.proxy.IProxy;
import org.zeith.squarry.api.particle.ParticleVortex;
import org.zeith.squarry.blocks.entity.TileFuelQuarry;

public class SQCommonProxy
		implements IProxy
{
	public static final ItemStack COAL = new ItemStack(Items.COAL);
	
	public void setup(IEventBus modBus)
	{
	}
	
	public ParticleVortex createQuarryVortex(TileFuelQuarry quarry)
	{
		return null;
	}
}
