package org.zeith.squarry.inventory.slots;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.CapabilityEnergy;

public class SlotFuelAndBattery
		extends SlotFurnaceFuel
{
	public SlotFuelAndBattery(Container inv, int id, int x, int y)
	{
		super(inv, id, x, y);
	}

	@Override
	public boolean mayPlace(ItemStack stack)
	{
		return stack.getCapability(CapabilityEnergy.ENERGY).isPresent() || super.mayPlace(stack);
	}
}
