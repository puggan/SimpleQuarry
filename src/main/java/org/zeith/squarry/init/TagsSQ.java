package org.zeith.squarry.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
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

		public static final TagKey<Block> QUARRY_BLACKLIST = sqtag("quarry_blacklist");
		public static final TagKey<Block> QUARRY_PIPE = sqtag("quarry_pipe");

		private static TagKey<Block> sqtag(String name)
		{
			return BlockTags.create(new ResourceLocation(SQConstants.MOD_ID, name));
		}
	}
}