package org.zeith.squarry;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zeith.hammerlib.api.items.CreativeTab;
import org.zeith.hammerlib.api.proxy.IProxy;
import org.zeith.hammerlib.core.adapter.LanguageAdapter;
import org.zeith.hammerlib.event.fml.FMLFingerprintCheckEvent;
import org.zeith.hammerlib.proxy.HLConstants;
import org.zeith.hammerlib.util.CommonMessages;
import org.zeith.hammerlib.util.mcf.Resources;
import org.zeith.squarry.blocks.BlockFuelQuarry;
import org.zeith.squarry.client.SQClientProxy;
import org.zeith.squarry.init.TagsSQ;

@Mod(SimpleQuarry.MOD_ID)
public class SimpleQuarry
{
	public static final String MOD_ID = "squarry";
	public static final SQCommonProxy PROXY = IProxy.create(() -> SQClientProxy::new, () -> SQCommonProxy::new);
	public static final Logger LOG = LogManager.getLogger("SimpleQuarry");
	
	@CreativeTab.RegisterTab
	public static final CreativeTab ITEM_GROUP = new CreativeTab(Resources.location(SQConstants.MOD_ID, "root"),
			b -> b.icon(BlockFuelQuarry.FUEL_QUARRY.asItem()::getDefaultInstance)
					.title(Component.translatable("itemGroup." + SQConstants.MOD_ID))
					.withTabsBefore(HLConstants.HL_TAB.id())
	);
	
	public SimpleQuarry(IEventBus modBus)
	{
		CommonMessages.printMessageOnIllegalRedistribution(SimpleQuarry.class,
				LOG, "SimpleQuarry", "https://www.curseforge.com/minecraft/mc-mods/simple-quarry"
		);
		
		LanguageAdapter.registerMod(SQConstants.MOD_ID);
		TagsSQ.init();
		SQConfig.reload();
		
		PROXY.setup(modBus);
		modBus.addListener(this::checkFingerprint);
	}
	
	private void checkFingerprint(FMLFingerprintCheckEvent e)
	{
		CommonMessages.printMessageOnFingerprintViolation(e, "97e852e9b3f01b83574e8315f7e77651c6605f2b455919a7319e9869564f013c",
				LOG, "SimpleQuarry", "https://www.curseforge.com/minecraft/mc-mods/simple-quarry"
		);
	}
	
	public static ResourceLocation id(String path)
	{
		return Resources.location(MOD_ID, path);
	}
}