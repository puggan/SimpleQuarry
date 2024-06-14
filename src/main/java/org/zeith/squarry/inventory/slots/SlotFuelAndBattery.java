package org.zeith.squarry.inventory.slots;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;

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
		var energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
		return (energyStorage != null && energyStorage.getEnergyStored() > 0 && energyStorage.canExtract()) || super.mayPlace(stack);
	}
}
