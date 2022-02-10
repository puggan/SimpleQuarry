package org.zeith.squarry.items;

import org.zeith.squarry.blocks.entity.TilePoweredQuarry;

public class ItemEfficiencyUpgrade extends ItemUpgrade
{
	private static final ItemEfficiencyUpgrade[] upgrades = new ItemEfficiencyUpgrade[3];
	
	public int lvl;
	
	public ItemEfficiencyUpgrade(int lvl, float save)
	{
		super(new Properties().stacksTo(1));
		if(upgrades[lvl] == null)
			upgrades[lvl] = this;
		this.lvl = lvl;
		quarryUseMultiplier = 1 / save;
	}
	
	@Override
	public boolean isCompatible(TilePoweredQuarry quarry)
	{
		if(lvl > 0 && !hasUpgrade(quarry, upgrades[lvl - 1]))
			return false;
		return !hasUpgrade(quarry, this);
	}
	
	@Override
	public boolean canStay(TilePoweredQuarry quarry, int index)
	{
		return lvl <= 0 || hasUpgrade(quarry, upgrades[lvl - 1]);
	}
}