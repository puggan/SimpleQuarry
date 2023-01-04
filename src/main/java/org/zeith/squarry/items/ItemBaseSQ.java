package org.zeith.squarry.items;

import net.minecraft.world.item.Item;
import org.zeith.squarry.SimpleQuarry;

public class ItemBaseSQ
		extends Item
{
	public ItemBaseSQ()
	{
		this(new Item.Properties());
	}
	
	public ItemBaseSQ(Properties properties)
	{
		super(properties);
		SimpleQuarry.ITEM_GROUP.add(this);
	}
}