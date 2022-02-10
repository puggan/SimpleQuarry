package org.zeith.squarry.items;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.squarry.blocks.entity.TilePoweredQuarry;
import org.zeith.squarry.init.ItemsSQ;

import java.util.Map;

@SimplyRegister
public class ItemSilkUpgrade
		extends ItemUpgrade
{

	public ItemSilkUpgrade()
	{
		super(new Properties().stacksTo(1));
		quarryUseMultiplier = 8;
	}

	@Override
	public void addEnchantments(TilePoweredQuarry quarry, Map<Enchantment, Integer> enchantmentMap)
	{
		enchantmentMap.put(Enchantments.SILK_TOUCH, 1);
	}

	@Override
	public boolean isCompatible(TilePoweredQuarry quarry)
	{
		return !hasUpgrade(quarry, this) && !hasUpgrade(quarry, ItemsSQ.UPGRADE_FORTUNE1) && !hasUpgrade(quarry, ItemsSQ.UPGRADE_AUTO_SMELT);
	}

	@Override
	public boolean canStay(TilePoweredQuarry quarry, int index)
	{
		return !hasUpgrade(quarry, ItemsSQ.UPGRADE_FORTUNE1) && !hasUpgrade(quarry, ItemsSQ.UPGRADE_AUTO_SMELT);
	}
}