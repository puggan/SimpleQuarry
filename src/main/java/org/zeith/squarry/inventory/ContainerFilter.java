package org.zeith.squarry.inventory;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.hammerlib.api.inv.IScreenContainer;
import org.zeith.hammerlib.api.inv.SimpleInventory;
import org.zeith.squarry.client.screen.ScreenFilter;
import org.zeith.squarry.init.ComponentTypesSQ;
import org.zeith.squarry.inventory.slots.SlotGhost;
import org.zeith.squarry.items.ItemFilterUpgrade;
import org.zeith.squarry.items.data.FilterDataComponent;

import javax.annotation.Nullable;
import java.util.*;

@SimplyRegister
public class ContainerFilter
		extends AbstractContainerMenu
		implements IScreenContainer
{
	@RegistryName("filter")
	public static final MenuType<ContainerFilter> FILTER = IMenuTypeExtension.create((windowId, playerInv, extraData) ->
	{
		FilterData tile = new FilterData(FilterDataComponent.STREAM_CODEC.decode(extraData));
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
				addSlot(new SlotGhost(data.inventory, x + y * 4, 53 + x * 18, 17 + y * 18, this::applyToHand));
	}
	
	@Override
	public boolean clickMenuButton(Player player, int id)
	{
		if(id == 0)
		{
			data.invert = !data.invert;
			applyToHand();
			
			return true;
		}
		
		if(id == 1)
		{
			data.useod = !data.useod;
			applyToHand();
			
			return true;
		}
		
		if(id == 2)
		{
			data.usemeta = !data.usemeta;
			applyToHand();
			
			return true;
		}
		
		return false;
	}
	
	public void applyToHand()
	{
		for(ItemStack item : inventory.items)
		{
			if(data.isThisFilter(item))
			{
				item.set(ComponentTypesSQ.FILTER_TYPE.get(), data.export());
				return;
			}
		}
	}
	
	@Override
	public boolean stillValid(Player player)
	{
		return data.isThisFilter(player.getItemInHand(InteractionHand.MAIN_HAND));
	}
	
	@Override
	public ItemStack quickMoveStack(Player player, int slot)
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
				return new ContainerFilter(player.getInventory(), windowId, new FilterData(
						filter.getOrDefault(ComponentTypesSQ.FILTER_TYPE.get(), FilterDataComponent.EMPTY)
				));
			}
		};
	}
	
	public static void openFilter(Player player, InteractionHand hand)
	{
		ItemStack hi = player.getItemInHand(hand);
		var type = ComponentTypesSQ.FILTER_TYPE.get();
		
		var comp = hi.getOrDefault(type, FilterDataComponent.EMPTY);
		if(Objects.equals(comp.filterId(), FilterDataComponent.EMPTY.filterId()))
		{
			comp = comp.toBuilder().filterId(UUID.randomUUID()).build();
			hi.set(type, comp);
		}
		
		if(!hi.isEmpty() && hi.getItem() instanceof ItemFilterUpgrade)
			if(player instanceof ServerPlayer mp)
				mp.openMenu(forStack(hi), buf -> FilterDataComponent.STREAM_CODEC.encode(buf, hi.getOrDefault(type, FilterDataComponent.EMPTY)));
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public Screen openScreen(Inventory inv, Component label)
	{
		return new ScreenFilter(this, inv, label);
	}
	
	public static class FilterData
	{
		private final FilterDataComponent stack;
		
		public SimpleInventory inventory = new SimpleInventory(12);
		
		public UUID id;
		public boolean invert;
		public boolean useod;
		public boolean usemeta;
		public boolean ignorenbt;
		
		public FilterData(FilterDataComponent stack)
		{
			this.stack = stack;
			id = stack.filterId();
			invert = stack.invertList();
			useod = stack.useTags();
			usemeta = stack.useDamage();
			ignorenbt = stack.ignoreComponents();
			List<ItemStack> filter = stack.filter();
			for(int i = 0; i < filter.size(); i++)
			{
				inventory.setStackInSlot(i, filter.get(i));
			}
		}
		
		public FilterDataComponent export()
		{
			return stack.toBuilder()
					.filterId(id)
					.invertList(invert)
					.useTags(useod)
					.useDamage(usemeta)
					.ignoreComponents(ignorenbt)
					.filter(inventory.items.stream().map(ItemStack::copy).toList())
					.build();
		}
		
		public boolean isThisFilter(ItemStack item)
		{
			return !item.isEmpty() && item.getItem() instanceof ItemFilterUpgrade up && Objects.equals(id, up.get(item).filterId());
		}
	}
}