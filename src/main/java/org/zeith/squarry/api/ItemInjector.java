package org.zeith.squarry.api;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class ItemInjector
{
	public static ItemStack inject(ItemStack item, BlockEntity tile, Direction capFace)
	{
		return tile != null ? tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, capFace).map(n -> inject(item, n)).orElse(item) : item;
	}

	public static ItemStack inject(ItemStack item, IItemHandler h)
	{
		for(int i = 0; h != null && i < h.getSlots() && !item.isEmpty(); ++i)
			item = h.insertItem(i, item, false);
		return item;
	}
}