package org.zeith.squarry.inventory.slots;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.zeith.squarry.blocks.entity.TilePoweredQuarry;
import org.zeith.squarry.items.ItemUpgrade;

public class SlotUpgrade
		extends Slot
{
	final TilePoweredQuarry quarry;

	public SlotUpgrade(Container inv, int id, int x, int y, TilePoweredQuarry quarry)
	{
		super(inv, id, x, y);
		this.quarry = quarry;
	}

	@Override
	public boolean mayPlace(ItemStack stack)
	{
		if(!stack.isEmpty() && stack.getItem() instanceof ItemUpgrade u)
			return u.isCompatible(quarry);
		return false;
	}
}