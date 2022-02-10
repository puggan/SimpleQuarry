package org.zeith.squarry.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import org.zeith.squarry.SQConfig;
import org.zeith.squarry.blocks.entity.TilePoweredQuarry;

public class ItemFillerUpgrade
		extends ItemUpgrade
{
	public ItemFillerUpgrade()
	{
		super(new Properties().stacksTo(1));
		quarryUseMultiplier = 1.5F;
	}

	@Override
	public void handleDrops(TilePoweredQuarry quarry, BlockPos pos, NonNullList<ItemStack> drops)
	{
		ListTag list = quarry.additionalTags.getList("RestorePositions", Tag.TAG_LONG);
		if(list.isEmpty())
			quarry.additionalTags.put("RestorePositions", list);
		list.add(LongTag.valueOf(pos.asLong()));

		for(int i = 0; i < drops.size(); ++i)
			if(ItemTags.DIRT.contains(drops.get(i).getItem()))
			{
				drops.remove(i);
				--i;
			}
	}

	@Override
	public void tick(TilePoweredQuarry quarry, int index)
	{
		if(quarry.isDone())
		{
			ListTag list = quarry.additionalTags.getList("RestorePositions", Tag.TAG_LONG);

			if(quarry.atTickRate(quarry.tickRate) && !list.isEmpty())
			{
				LongTag pos = (LongTag) list.remove(list.size() - 1);
				BlockPos bp = BlockPos.of(pos.getAsLong());
				Level w = quarry.getLevel();
				if(!w.isClientSide && (w.isEmptyBlock(bp) || w.getBlockState(bp).getBlock() instanceof LiquidBlock))
					w.setBlock(bp, bp.getY() < 0 ? Blocks.COBBLED_DEEPSLATE.defaultBlockState() : Blocks.COARSE_DIRT.defaultBlockState(), 3);
			} else if(list.isEmpty())
				quarry.additionalTags.remove("RestorePositions");
		}
	}

	@Override
	public boolean isCompatible(TilePoweredQuarry quarry)
	{
		return !hasUpgrade(quarry, this);
	}

	@Override
	public boolean canStay(TilePoweredQuarry quarry, int index)
	{
		return true;
	}
}