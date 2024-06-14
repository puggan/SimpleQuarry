package org.zeith.squarry.api;

import com.google.common.collect.Lists;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.List;

public class ItemStackList
		extends NonNullList<ItemStack>
		implements INBTSerializable<CompoundTag>
{
	public ItemStackList(List<ItemStack> p_122777_, @Nullable ItemStack p_122778_)
	{
		super(p_122777_, p_122778_);
	}

	public static ItemStackList createList()
	{
		return new ItemStackList(Lists.newArrayList(), null);
	}

	@Override
	public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider)
	{
		return ContainerHelper.saveAllItems(new CompoundTag(), this, provider);
	}
	
	@Override
	public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt)
	{
		ContainerHelper.loadAllItems(nbt, this, provider);
	}
}