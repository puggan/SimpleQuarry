package org.zeith.squarry.blocks.entity;

import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.hammerlib.api.forge.BlockAPI;
import org.zeith.hammerlib.api.inv.SimpleInventory;
import org.zeith.hammerlib.api.io.NBTSerializable;
import org.zeith.squarry.SQConfig;
import org.zeith.squarry.blocks.BlockPoweredQuarry;
import org.zeith.squarry.inventory.ContainerPoweredQuarry;
import org.zeith.squarry.items.ItemUpgrade;

@SimplyRegister
public class TilePoweredQuarry
		extends TileFuelQuarry
		implements IEnergyStorage
{
	@RegistryName("powered_quarry")
	public static final BlockEntityType<TilePoweredQuarry> POWERED_QUARRY = BlockAPI.createBlockEntityType(TilePoweredQuarry::new, BlockPoweredQuarry.POWERED_QUARRY);
	
	@NBTSerializable
	public final SimpleInventory invUpgrades = new SimpleInventory(5);
	
	public CompoundTag additionalTags = new CompoundTag();
	
	protected TilePoweredQuarry(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
		tickRate = SQConfig.getPoweredQuarryTickRate();
	}
	
	@Override
	protected double getQFCapacity()
	{
		return 256000.0;
	}
	
	@Override
	public double getUsageMult()
	{
		double val = super.getUsageMult();
		for(int i = 0; i < 5; ++i)
			if(getUpgrade(i) != null)
				val *= getUpgrade(i).quarryUseMultiplierServer;
		return val;
	}
	
	public ItemStack getUpgradeStack(int index)
	{
		return invUpgrades.getStackInSlot(index % 5);
	}
	
	public ItemUpgrade getUpgrade(int index)
	{
		ItemStack stack = getUpgradeStack(index);
		if(!stack.isEmpty() && stack.getItem() instanceof ItemUpgrade u)
			return u;
		return null;
	}
	
	public ItemUpgrade[] getUpgrades()
	{
		ItemUpgrade[] upgrades = new ItemUpgrade[5];
		for(int i = 0; i < 5; ++i)
			upgrades[i] = getUpgrade(i);
		return upgrades;
	}
	
	@Override
	public void update()
	{
		if(!SQConfig.isPoweredQuarry())
			return;
		
		ItemStack stack = inventory.getStackInSlot(0);
		
		energyItem:
		if(!stack.isEmpty())
		{
			var pc = stack.getCapability(Capabilities.EnergyStorage.ITEM);
			if(pc == null) break energyItem;
			int canExtract = pc.extractEnergy(pc.getEnergyStored(), true);
			canExtract = Math.min(receiveEnergy(canExtract, true), canExtract);
			pc.extractEnergy(canExtract, false);
			receiveEnergy(canExtract, false);
		}
		
		for(int i = 0; i < invUpgrades.getContainerSize(); ++i)
		{
			ItemUpgrade up = getUpgrade(i);
			if(up != null && !up.canStay(this, i))
			{
				ItemStack s = invUpgrades.getStackInSlot(i).copy();
				invUpgrades.setItem(i, ItemStack.EMPTY);
				queueItems.add(s);
			} else if(up != null)
				up.tick(this, i);
		}
		
		super.update();
		tickRate = SQConfig.getPoweredQuarryTickRate();
	}
	
	@Override
	public NonNullList<ItemStack> makeDrops(BlockPos pos, BlockState state)
	{
		NonNullList<ItemStack> drops = super.makeDrops(pos, state);
		for(int i = 0; i < invUpgrades.getContainerSize(); ++i)
			if(getUpgrade(i) != null)
				getUpgrade(i).handleDrops(this, pos, drops);
		return drops;
	}
	
	@Override
	public void addQueueItem(ItemStack e)
	{
		if(e.isEmpty())
			return;
		
		for(int i = 0; i < invUpgrades.getContainerSize(); ++i)
		{
			ItemUpgrade up = getUpgrade(i);
			if(up != null)
				try
				{
					e = up.handlePickup(e, this, i);
				} catch(Throwable err)
				{
					err.printStackTrace();
				}
		}
		
		if(!e.isEmpty())
			super.addQueueItem(e);
	}
	
	@Override
	public AbstractContainerMenu openContainer(Player player, int windowId)
	{
		if(!SQConfig.isPoweredQuarry()) return null;
		return new ContainerPoweredQuarry(player, windowId, this);
	}
	
	@Override
	protected Block getQuarryBlock()
	{
		return BlockPoweredQuarry.POWERED_QUARRY;
	}
	
	@Override
	public void addToolEnchantments(ItemEnchantments.Mutable enchantmentMap)
	{
		for(int i = 0; i < invUpgrades.getContainerSize(); ++i)
		{
			ItemUpgrade iu = getUpgrade(i);
			if(iu != null)
				iu.addEnchantments(this, enchantmentMap);
		}
	}
	
	@Override
	public void readNBT(CompoundTag nbt, HolderLookup.Provider provider)
	{
		super.readNBT(nbt, provider);
		additionalTags = nbt.getCompound("AdditionalTags");
	}
	
	@Override
	public CompoundTag writeNBT(CompoundTag nbt, HolderLookup.Provider provider)
	{
		nbt = super.writeNBT(nbt, provider);
		nbt.put("AdditionalTags", additionalTags.copy());
		return nbt;
	}
	
	@Override
	public int receiveEnergy(int maxReceive, boolean simulate)
	{
		if(!SQConfig.isPoweredQuarry())
			return 0;
		float fec = SQConfig.getFeConversion(), hec = SQConfig.getHeConversion();
		if(maxReceive >= fec)
			return (int) (storage.consumeQF(null, maxReceive / fec, simulate) * fec);
		else // Otherwise, convert at half efficiency.
			return (int) (storage.consumeQF(null, maxReceive / hec, simulate) * hec);
	}
	
	@Override
	public int extractEnergy(int maxExtract, boolean simulate)
	{
		return 0;
	}
	
	@Override
	public int getEnergyStored()
	{
		return 0;
	}
	
	@Override
	public int getMaxEnergyStored()
	{
		return 1000;
	}
	
	@Override
	public boolean canExtract()
	{
		return false;
	}
	
	@Override
	public boolean canReceive()
	{
		return true;
	}
	
	@Override
	public void dropEverything(Level world, BlockPos pos)
	{
		super.dropEverything(world, pos);
		Containers.dropContents(world, pos, invUpgrades);
	}
}