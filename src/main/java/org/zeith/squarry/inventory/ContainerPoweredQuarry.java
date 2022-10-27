package org.zeith.squarry.inventory;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.zeith.hammerlib.api.forge.ContainerAPI;
import org.zeith.hammerlib.api.inv.IScreenContainer;
import org.zeith.squarry.blocks.entity.TilePoweredQuarry;
import org.zeith.squarry.client.screen.ScreenPoweredQuarry;
import org.zeith.squarry.inventory.slots.SlotFuelAndBattery;
import org.zeith.squarry.inventory.slots.SlotUpgrade;

public class ContainerPoweredQuarry
		extends AbstractContainerMenu
		implements IScreenContainer
{
	public TilePoweredQuarry tile;

	public ContainerPoweredQuarry(Player player, int windowId, TilePoweredQuarry tile)
	{
		super(ContainerAPI.TILE_CONTAINER, windowId);
		this.tile = tile;

		int x;
		for(x = 0; x < 3; ++x)
			for(int y = 0; y < 9; ++y)
				addSlot(new Slot(player.getInventory(), y + x * 9 + 9, 8 + y * 18, 84 + x * 18));

		for(x = 0; x < 9; ++x)
			addSlot(new Slot(player.getInventory(), x, 8 + x * 18, 142));

		addSlot(new SlotFuelAndBattery(tile.inventory, 0, 25, 49));
		for(int i = 4; i >= 0; --i)
			addSlot(new SlotUpgrade(tile.invUpgrades, i, 62 + i * 18, 59, tile));
	}

	@Override
	public boolean stillValid(Player playerIn)
	{
		return tile.getBlockPos().closerToCenterThan(playerIn.position(), 64) && !tile.isRemoved();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public Screen openScreen(Inventory inv, Component label)
	{
		return new ScreenPoweredQuarry(this, inv, label);
	}
	
	@Override
	public ItemStack quickMoveStack(Player player, int slotId)
	{
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(slotId);
		if(slot != null && slot.hasItem())
		{
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			if(slot.container == player.getInventory())
			{
				if(!this.moveItemStackTo(itemstack1, 36, slots.size(), true))
					return ItemStack.EMPTY;
			} else
			{
				if(!this.moveItemStackTo(itemstack1, 0, 36, true))
					return ItemStack.EMPTY;
			}
			
			if(itemstack1.isEmpty())
			{
				slot.set(ItemStack.EMPTY);
			} else
			{
				slot.setChanged();
			}
			
			if(itemstack1.getCount() == itemstack.getCount())
			{
				return ItemStack.EMPTY;
			}
			
			slot.onTake(player, itemstack1);
		}
		
		return itemstack;
	}
}