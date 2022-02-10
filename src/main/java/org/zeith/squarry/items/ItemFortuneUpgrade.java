package org.zeith.squarry.items;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import org.zeith.squarry.blocks.entity.TilePoweredQuarry;
import org.zeith.squarry.init.ItemsSQ;

import java.util.Map;

public class ItemFortuneUpgrade
		extends ItemUpgrade
{
	private static final ItemFortuneUpgrade[] upgrades = new ItemFortuneUpgrade[3];

	public int lvl;

	public ItemFortuneUpgrade(int lvl)
	{
		super(new Properties().stacksTo(1));
		if(upgrades[lvl] == null)
			upgrades[lvl] = this;
		this.lvl = lvl;
		quarryUseMultiplier = 2;
	}

	@Override
	public void addEnchantments(TilePoweredQuarry quarry, Map<Enchantment, Integer> enchantmentMap)
	{
		int fortune = 0;
		if(ItemUpgrade.hasUpgrade(quarry, ItemsSQ.UPGRADE_FORTUNE1))
			++fortune;
		if(ItemUpgrade.hasUpgrade(quarry, ItemsSQ.UPGRADE_FORTUNE2) && fortune == 1)
			++fortune;
		if(ItemUpgrade.hasUpgrade(quarry, ItemsSQ.UPGRADE_FORTUNE3) && fortune == 2)
			++fortune;
		enchantmentMap.put(Enchantments.BLOCK_FORTUNE, fortune);
	}

	@Override
	public boolean isCompatible(TilePoweredQuarry quarry)
	{
		if(lvl > 0 && !hasUpgrade(quarry, upgrades[lvl - 1]))
			return false;
		return !hasUpgrade(quarry, this) && !hasUpgrade(quarry, ItemsSQ.UPGRADE_SILK);
	}

	@Override
	public boolean canStay(TilePoweredQuarry quarry, int index)
	{
		if(lvl > 0 && !hasUpgrade(quarry, upgrades[lvl - 1]))
			return false;
		return !hasUpgrade(quarry, ItemsSQ.UPGRADE_SILK);
	}
}