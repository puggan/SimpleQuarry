package org.zeith.squarry.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import org.zeith.squarry.blocks.entity.TilePoweredQuarry;
import org.zeith.squarry.init.ItemsSQ;

import java.util.Optional;

public class ItemAutoSmeltUpgrade
		extends ItemUpgrade
{
	public ItemAutoSmeltUpgrade()
	{
		super(new Properties().stacksTo(1));
		quarryUseMultiplier = 4;
	}
	
	@Override
	public void handleDrops(TilePoweredQuarry quarry, BlockPos pos, NonNullList<ItemStack> drops)
	{
		var rm = quarry.getLevel().getRecipeManager();
		for(int i = 0; i < drops.size(); ++i)
		{
			SingleRecipeInput ctr = new SingleRecipeInput(drops.get(i));
			final int j = i;
			
			RecipeHolder<? extends AbstractCookingRecipe> opt = rm
					.getRecipeFor(RecipeType.SMELTING, ctr, quarry.getLevel())
					.orElse(null);
			
			if(opt == null)
				opt = rm
						.getRecipeFor(RecipeType.BLASTING, ctr, quarry.getLevel())
						.orElse(null);
			
			Optional.ofNullable(opt).map(RecipeHolder::value).ifPresent(recipe ->
			{
				ItemStack res = recipe.assemble(ctr, quarry.getLevel().registryAccess());
				if(!res.isEmpty())
				{
					res.setCount(res.getCount() * drops.get(j).getCount());
					drops.set(j, res);
				}
			});
		}
	}
	
	@Override
	public boolean isCompatible(TilePoweredQuarry quarry)
	{
		return !hasUpgrade(quarry, this) && !hasUpgrade(quarry, ItemsSQ.UPGRADE_SILK);
	}
	
	@Override
	public boolean canStay(TilePoweredQuarry quarry, int index)
	{
		return !hasUpgrade(quarry, ItemsSQ.UPGRADE_SILK);
	}
}