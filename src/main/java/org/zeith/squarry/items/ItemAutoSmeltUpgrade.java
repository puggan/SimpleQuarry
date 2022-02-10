package org.zeith.squarry.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
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
		for(int i = 0; i < drops.size(); ++i)
		{
			FakeSmeltingContainer ctr = new FakeSmeltingContainer(drops.get(i));
			final int j = i;

			Optional<? extends AbstractCookingRecipe> opt = quarry.getLevel()
					.getRecipeManager()
					.getRecipeFor(RecipeType.SMELTING, ctr, quarry.getLevel());

			if(opt.isEmpty())
				opt = quarry.getLevel()
						.getRecipeManager()
						.getRecipeFor(RecipeType.BLASTING, ctr, quarry.getLevel());

			opt.ifPresent(recipe ->
			{
				ItemStack res = recipe.assemble(ctr);
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

	public record FakeSmeltingContainer(ItemStack stack)
			implements Container
	{
		@Override
		public int getContainerSize()
		{
			return 0;
		}

		@Override
		public boolean isEmpty()
		{
			return false;
		}

		@Override
		public ItemStack getItem(int p_18941_)
		{
			return stack;
		}

		@Override
		public ItemStack removeItem(int p_18942_, int p_18943_)
		{
			return null;
		}

		@Override
		public ItemStack removeItemNoUpdate(int p_18951_)
		{
			return null;
		}

		@Override
		public void setItem(int p_18944_, ItemStack p_18945_)
		{

		}

		@Override
		public void setChanged()
		{

		}

		@Override
		public boolean stillValid(Player p_18946_)
		{
			return false;
		}

		@Override
		public void clearContent()
		{

		}
	}
}