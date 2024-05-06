package org.zeith.squarry.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.util.CommonMessages;

import java.util.List;

public class ItemMaterialSQ
		extends ItemBaseSQ
{
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flags)
	{
		super.appendHoverText(stack, level, tooltip, flags);
		tooltip.add(CommonMessages.CRAFTING_MATERIAL);
	}
}