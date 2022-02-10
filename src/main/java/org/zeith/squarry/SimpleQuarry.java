package org.zeith.squarry;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zeith.hammerlib.core.adapter.LanguageAdapter;
import org.zeith.squarry.blocks.BlockFuelQuarry;
import org.zeith.squarry.client.SQClientProxy;
import org.zeith.squarry.init.TagsSQ;

@Mod("squarry")
public class SimpleQuarry
{
	public static final SQCommonProxy PROXY = DistExecutor.unsafeRunForDist(() -> SQClientProxy::new, () -> SQCommonProxy::new);
	public static final Logger LOG = LogManager.getLogger("SimpleQuarry");

	public static final CreativeModeTab ITEM_GROUP = new CreativeModeTab(SQConstants.MOD_ID)
	{
		@Override
		public ItemStack makeIcon()
		{
			return new ItemStack(BlockFuelQuarry.FUEL_QUARRY);
		}
	};

	public SimpleQuarry()
	{
		LanguageAdapter.registerMod(SQConstants.MOD_ID);
		TagsSQ.init();
		SQConfig.reload();
	}
}