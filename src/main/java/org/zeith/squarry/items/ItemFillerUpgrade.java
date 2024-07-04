package org.zeith.squarry.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import org.zeith.hammerlib.util.java.Cast;
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
		
		int end = list.size();
		int start = Math.max(0, end - 256);
		long lv = pos.asLong();
		boolean shouldAdd = true;
		for(int i = end - 1; i >= start; --i)
		{
			if(list.get(i) instanceof NumericTag lt && lv == lt.getAsLong())
			{
				shouldAdd = false;
				break;
			}
		}
		if(shouldAdd) list.add(LongTag.valueOf(lv));
		
		for(int i = 0; i < drops.size(); ++i)
			if(drops.get(i).is(ItemTags.DIRT))
			{
				drops.remove(i);
				--i;
			}
	}
	
	@Override
	public void tick(TilePoweredQuarry quarry, int index)
	{
		if(!quarry.isDone() || !quarry.atTickRate(quarry.tickRate)) return;
		
		ListTag list = quarry.additionalTags.getList("RestorePositions", Tag.TAG_LONG);
		
		int it = 0;
		Level w = quarry.getLevel();
		if(w != null && !w.isClientSide)
			while(!list.isEmpty())
			{
				if(++it > 512) break;
				NumericTag pos = Cast.cast(list.remove(list.size() - 1), NumericTag.class);
				if(pos == null) continue;
				BlockPos bp = BlockPos.of(pos.getAsLong());
				if((w.isEmptyBlock(bp) || w.getBlockState(bp).getBlock() instanceof LiquidBlock))
				{
					w.setBlock(bp, bp.getY() < 0 ? Blocks.COBBLED_DEEPSLATE.defaultBlockState() : Blocks.COARSE_DIRT.defaultBlockState(), 3);
					break;
				}
			}
		
		if(list.isEmpty())
			quarry.additionalTags.remove("RestorePositions");
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