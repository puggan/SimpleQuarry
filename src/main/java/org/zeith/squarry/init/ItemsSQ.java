package org.zeith.squarry.init;

import net.minecraft.world.item.Item;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.squarry.SimpleQuarry;
import org.zeith.squarry.items.*;

@SimplyRegister
public class ItemsSQ
{
	@RegistryName("upgrade_base")
	public static final Item UPGRADE_BASE = new Item(new Item.Properties().tab(SimpleQuarry.ITEM_GROUP));

	@RegistryName("upgrade_silk")
	public static final ItemUpgrade UPGRADE_SILK = new ItemSilkUpgrade();

	@RegistryName("upgrade_filter")
	public static final ItemFilterUpgrade UPGRADE_FILTER = new ItemFilterUpgrade();

	@RegistryName("upgrade_filler")
	public static final ItemFillerUpgrade UPGRADE_FILLER = new ItemFillerUpgrade();

	@RegistryName("upgrade_auto_smelt")
	public static final ItemUpgrade UPGRADE_AUTO_SMELT = new ItemAutoSmeltUpgrade();

	@RegistryName("upgrade_fortune_1")
	public static final ItemUpgrade UPGRADE_FORTUNE1 = new ItemFortuneUpgrade(0);

	@RegistryName("upgrade_fortune_2")
	public static final ItemUpgrade UPGRADE_FORTUNE2 = new ItemFortuneUpgrade(1);

	@RegistryName("upgrade_fortune_3")
	public static final ItemUpgrade UPGRADE_FORTUNE3 = new ItemFortuneUpgrade(2);

	@RegistryName("upgrade_efficiency_1")
	public static final ItemUpgrade UPGRADE_EFFICIENCY1 = new ItemEfficiencyUpgrade(0, 1.05F);

	@RegistryName("upgrade_efficiency_2")
	public static final ItemUpgrade UPGRADE_EFFICIENCY2 = new ItemEfficiencyUpgrade(1, 1.1F);

	@RegistryName("upgrade_efficiency_3")
	public static final ItemUpgrade UPGRADE_EFFICIENCY3 = new ItemEfficiencyUpgrade(2, 1.35F);
}