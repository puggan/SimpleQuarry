package org.zeith.squarry.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.zeith.hammerlib.api.fml.IRegisterListener;
import org.zeith.squarry.blocks.entity.TilePoweredQuarry;

import java.util.*;

public abstract class ItemUpgrade
		extends ItemBaseSQ
		implements IRegisterListener
{
	private static final List<ItemUpgrade> UPGRADE_LIST = new ArrayList<>();
	public static final List<ItemUpgrade> ALL_UPGRADES = Collections.unmodifiableList(UPGRADE_LIST);
	
	protected float quarryUseMultiplier = 1;
	
	public float quarryUseMultiplierServer = 1;
	public float quarryUseMultiplierClient = 1;
	
	public ItemUpgrade(Properties props)
	{
		super(props);
	}
	
	public float getBaseQuarryUseMultiplier()
	{
		return quarryUseMultiplier;
	}
	
	@Override
	public void onPostRegistered()
	{
		UPGRADE_LIST.add(this);
		quarryUseMultiplierClient = quarryUseMultiplierServer = quarryUseMultiplier;
	}
	
	public void addEnchantments(TilePoweredQuarry quarry, Map<Enchantment, Integer> enchantmentMap)
	{
	}
	
	public void handleDrops(TilePoweredQuarry quarry, BlockPos pos, NonNullList<ItemStack> drops)
	{
	}
	
	public boolean canStay(TilePoweredQuarry quarry, int index)
	{
		return true;
	}
	
	public ItemStack handlePickup(ItemStack stack, TilePoweredQuarry quarry, int index)
	{
		return stack;
	}
	
	public boolean isCompatible(TilePoweredQuarry quarry)
	{
		return true;
	}
	
	public void tick(TilePoweredQuarry quarry, int index)
	{
	}
	
	public static boolean hasUpgrade(TilePoweredQuarry quarry, ItemUpgrade upgrade)
	{
		for(ItemUpgrade up : quarry.getUpgrades())
			if(up == upgrade)
				return true;
		return false;
	}
}