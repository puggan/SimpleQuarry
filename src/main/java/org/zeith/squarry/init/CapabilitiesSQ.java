package org.zeith.squarry.init;

import net.minecraft.core.Direction;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.zeith.squarry.blocks.entity.TilePoweredQuarry;

import static org.zeith.squarry.blocks.entity.TilePoweredQuarry.POWERED_QUARRY;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class CapabilitiesSQ
{
	@SubscribeEvent
	public static void capabilities(RegisterCapabilitiesEvent e)
	{
		e.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, POWERED_QUARRY,
				(TilePoweredQuarry object, Direction context) -> object
		);
	}
}