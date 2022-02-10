package org.zeith.squarry.inventory;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.network.NetworkHooks;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.hammerlib.api.inv.IScreenContainer;
import org.zeith.hammerlib.api.inv.SimpleInventory;
import org.zeith.squarry.client.screen.ScreenFilter;
import org.zeith.squarry.inventory.slots.SlotGhost;
import org.zeith.squarry.items.ItemFilterUpgrade;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

@SimplyRegister
public class ContainerFilter
		extends AbstractContainerMenu
		implements IScreenContainer
{
	@RegistryName("filter")
	public static final MenuType<ContainerFilter> FILTER = IForgeMenuType.create((windowId, playerInv, extraData) ->
	{
		FilterData tile = new FilterData(extraData.readItem());
		return new ContainerFilter(playerInv, windowId, tile);
	});

	public final Inventory inventory;
	public final FilterData data;

	public ContainerFilter(Inventory inventory, int windowId, FilterData data)
	{
		super(FILTER, windowId);
		this.inventory = inventory;
		this.data = data;

		int x;
		for(x = 0; x < 3; ++x)
			for(int y = 0; y < 9; ++y)
				addSlot(new Slot(inventory, y + x * 9 + 9, 8 + y * 18, 84 + x * 18));

		for(x = 0; x < 9; ++x)
			addSlot(new Slot(inventory, x, 8 + x * 18, 142));

		for(x = 0; x < 4; ++x)
			for(int y = 0; y < 3; ++y)
				addSlot(new SlotGhost(data.inventory, x + y * 4, 53 + x * 18, 17 + y * 18, data::getStack));
	}

	@Override
	public boolean clickMenuButton(Player player, int id)
	{
		if(id == 0)
		{
			data.invert = !data.invert;
			data.getStack();

			return true;
		}

		if(id == 1)
		{
			data.useod = !data.useod;
			data.getStack();

			return true;
		}

		if(id == 2)
		{
			data.usemeta = !data.usemeta;
			data.getStack();

			return true;
		}

		return false;
	}

	@Override
	public boolean stillValid(Player player)
	{
		return data.isThisFilter(player.getItemInHand(InteractionHand.MAIN_HAND));
	}

	@Override
	public ItemStack quickMoveStack(Player p_38941_, int p_38942_)
	{
		return ItemStack.EMPTY;
	}

	private static MenuProvider forStack(ItemStack filter)
	{
		return new MenuProvider()
		{
			@Override
			public Component getDisplayName()
			{
				return filter.getDisplayName();
			}

			@Nullable
			@Override
			public AbstractContainerMenu createMenu(int windowId, Inventory playerInv, Player player)
			{
				return new ContainerFilter(player.getInventory(), windowId, new FilterData(filter));
			}
		};
	}

	public static void openFilter(Player player, InteractionHand hand)
	{
		ItemStack hi = player.getItemInHand(hand);
		if(!hi.hasTag())
			hi.setTag(new CompoundTag());
		if(!hi.getTag().contains("FilterId"))
			hi.getTag().putUUID("FilterId", UUID.randomUUID());

		if(!hi.isEmpty() && hi.getItem() instanceof ItemFilterUpgrade)
			if(player instanceof ServerPlayer)
				NetworkHooks.openGui((ServerPlayer) player, forStack(hi), buf -> buf.writeItem(hi));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public Screen openScreen(Inventory inv, Component label)
	{
		return new ScreenFilter(this, inv, label);
	}

	public static class FilterData
	{
		private final ItemStack stack;
		public SimpleInventory inventory = new SimpleInventory(12);

		public UUID id;
		public boolean invert;
		public boolean useod;
		public boolean usemeta;
		public boolean ignorenbt;

		public FilterData(ItemStack stack)
		{
			this.stack = stack;
			CompoundTag nbt = stack.getTag();
			if(nbt != null)
			{
				id = nbt.getUUID("FilterId");
				invert = nbt.getBoolean("InvertList");
				useod = nbt.getBoolean("OreDictionary");
				usemeta = nbt.getBoolean("Metadata");
				ignorenbt = nbt.getBoolean("IgnoreNBT");
				inventory.readFromNBT(nbt.getList("Filter", Tag.TAG_COMPOUND));
			}
		}

		public ItemStack getStack()
		{
			CompoundTag nbt = stack.getTag();
			if(nbt == null)
				nbt = new CompoundTag();
			nbt.putUUID("FilterId", id);
			nbt.putBoolean("InvertList", invert);
			nbt.putBoolean("OreDictionary", useod);
			nbt.putBoolean("Metadata", usemeta);
			nbt.putBoolean("IgnoreNBT", ignorenbt);
			nbt.put("Filter", inventory.writeToNBT(new ListTag()));
			stack.setTag(nbt);
			return stack;
		}

		public boolean isThisFilter(ItemStack item)
		{
			return !item.isEmpty() && item.getItem() instanceof ItemFilterUpgrade && item.hasTag() && Objects.equals(id, item.getTag().getUUID("FilterId"));
		}
	}
}