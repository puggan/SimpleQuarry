package org.zeith.squarry.inventory.slots;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

public class SlotFurnaceFuel
		extends Slot
{
	public SlotFurnaceFuel(Container inv, int id, int x, int y)
	{
		super(inv, id, x, y);
	}

	@Override
	public boolean mayPlace(ItemStack stack)
	{
		return AbstractFurnaceBlockEntity.isFuel(stack);
	}
}