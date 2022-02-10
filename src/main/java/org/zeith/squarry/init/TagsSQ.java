package org.zeith.squarry.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import org.zeith.squarry.SQConstants;

public class TagsSQ
{
	public static void init()
	{
		Blocks.init();
	}

	public static class Blocks
	{
		private static void init()
		{
		}

		public static final Tags.IOptionalNamedTag<Block> QUARRY_BLACKLIST = sqtag("quarry_blacklist");

		private static Tags.IOptionalNamedTag<Block> sqtag(String name)
		{
			return BlockTags.createOptional(new ResourceLocation(SQConstants.MOD_ID, name));
		}
	}
}