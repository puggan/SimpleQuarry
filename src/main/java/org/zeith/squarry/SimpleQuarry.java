package org.zeith.squarry;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zeith.hammerlib.api.items.CreativeTab;
import org.zeith.hammerlib.core.adapter.LanguageAdapter;
import org.zeith.hammerlib.event.fml.FMLFingerprintCheckEvent;
import org.zeith.hammerlib.proxy.HLConstants;
import org.zeith.hammerlib.util.CommonMessages;
import org.zeith.squarry.blocks.BlockFuelQuarry;
import org.zeith.squarry.client.SQClientProxy;
import org.zeith.squarry.init.TagsSQ;

@Mod(SimpleQuarry.MOD_ID)
public class SimpleQuarry
{
	public static final String MOD_ID = "squarry";
	public static final SQCommonProxy PROXY = DistExecutor.unsafeRunForDist(() -> SQClientProxy::new, () -> SQCommonProxy::new);
	public static final Logger LOG = LogManager.getLogger("SimpleQuarry");
	
	@CreativeTab.RegisterTab
	public static final CreativeTab ITEM_GROUP = new CreativeTab(new ResourceLocation(SQConstants.MOD_ID, "root"),
			b -> b.icon(BlockFuelQuarry.FUEL_QUARRY.asItem()::getDefaultInstance)
					.title(Component.translatable("itemGroup." + SQConstants.MOD_ID))
					.withTabsBefore(HLConstants.HL_TAB.id())
	);
	
	public SimpleQuarry()
	{
		CommonMessages.printMessageOnIllegalRedistribution(SimpleQuarry.class,
				LOG, "SimpleQuarry", "https://www.curseforge.com/minecraft/mc-mods/simple-quarry");
		
		LanguageAdapter.registerMod(SQConstants.MOD_ID);
		TagsSQ.init();
		SQConfig.reload();
		
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::checkFingerprint);
	}
	
	private void checkFingerprint(FMLFingerprintCheckEvent e)
	{
		CommonMessages.printMessageOnFingerprintViolation(e, "97e852e9b3f01b83574e8315f7e77651c6605f2b455919a7319e9869564f013c",
				LOG, "SimpleQuarry", "https://www.curseforge.com/minecraft/mc-mods/simple-quarry");
	}
	
	public static ResourceLocation id(String path)
	{
		return new ResourceLocation(MOD_ID, path);
	}
}