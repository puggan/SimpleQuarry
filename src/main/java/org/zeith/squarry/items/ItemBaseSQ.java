package org.zeith.squarry.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.Nullable;
import org.zeith.squarry.SimpleQuarry;

import java.util.List;

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
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Item.TooltipContext level, List<Component> tooltip, TooltipFlag flags)
	{
		for(int i = 0; ; ++i)
		{
			var id = getDescriptionId(stack) + ".tooltip" + i;
			var t = Component.translatable(id);
			if(t.getString().equals(id)) break;
			tooltip.add(t.withStyle(ChatFormatting.GRAY));
		}
		super.appendHoverText(stack, level, tooltip, flags);
	}
}