package org.zeith.squarry.api;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

public class ItemInjector
{
	public static ItemStack inject(ItemStack item, Level level, BlockPos pos, Direction capFace)
	{
		var cap = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, capFace);
		return cap != null ? inject(item, cap) : item;
	}
	
	public static ItemStack inject(ItemStack item, IItemHandler h)
	{
		for(int i = 0; h != null && i < h.getSlots() && !item.isEmpty(); ++i)
			item = h.insertItem(i, item, false);
		return item;
	}
}