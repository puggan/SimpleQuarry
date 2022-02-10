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
import org.zeith.squarry.blocks.entity.TileFuelQuarry;
import org.zeith.squarry.client.screen.ScreenFuelQuarry;
import org.zeith.squarry.inventory.slots.SlotFurnaceFuel;

public class ContainerFuelQuarry
		extends AbstractContainerMenu
		implements IScreenContainer
{
	public TileFuelQuarry tile;

	public ContainerFuelQuarry(Player player, int windowId, TileFuelQuarry tile)
	{
		super(ContainerAPI.TILE_CONTAINER, windowId);
		this.tile = tile;

		int x;
		for(x = 0; x < 3; ++x)
			for(int y = 0; y < 9; ++y)
				addSlot(new Slot(player.getInventory(), y + x * 9 + 9, 8 + y * 18, 84 + x * 18));

		for(x = 0; x < 9; ++x)
			addSlot(new Slot(player.getInventory(), x, 8 + x * 18, 142));

		addSlot(new SlotFurnaceFuel(tile.inventory, 0, 80, 49));
	}

	@Override
	public boolean stillValid(Player playerIn)
	{
		return tile.getBlockPos().distSqr(playerIn.position(), false) < 64D && !tile.isRemoved();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public Screen openScreen(Inventory inv, Component label)
	{
		return new ScreenFuelQuarry(this, inv, label);
	}

	@Override
	public ItemStack quickMoveStack(Player player, int slot)
	{
		return ItemStack.EMPTY;
	}
}