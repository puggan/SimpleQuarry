package org.zeith.squarry.items;

import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.zeith.squarry.blocks.entity.TilePoweredQuarry;
import org.zeith.squarry.init.ComponentTypesSQ;
import org.zeith.squarry.init.ItemsSQ;
import org.zeith.squarry.inventory.ContainerFilter;
import org.zeith.squarry.items.data.FilterDataComponent;

import java.util.*;
import java.util.stream.Collectors;

public class ItemFilterUpgrade
		extends ItemUpgrade
{
	public ItemFilterUpgrade()
	{
		super(new Properties().stacksTo(1));
		quarryUseMultiplier = 1;
	}
	
	public FilterDataComponent get(ItemStack stack)
	{
		return stack.getOrDefault(ComponentTypesSQ.FILTER_TYPE.get(), FilterDataComponent.EMPTY);
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
	{
		if(hand == InteractionHand.MAIN_HAND)
			ContainerFilter.openFilter(player, hand);
		return super.use(level, player, hand);
	}
	
	public static boolean matches(ItemStack filter, ItemStack input)
	{
		var nbt = filter.getItem() instanceof ItemFilterUpgrade up ? up.get(filter) : FilterDataComponent.EMPTY;
		
		boolean invert = nbt.invertList();
		boolean useod = nbt.useTags();
		boolean usemeta = nbt.useDamage();
		boolean ignorenbt = nbt.ignoreComponents();
		
		boolean applies = false;
		
		for(var ft : nbt.filter())
		{
			if(ft.isEmpty()) continue;
			
			if(ft.getItem() instanceof ItemFilterUpgrade && matches(ft, input))
			{
				applies = true;
				break;
			} else
			{
				boolean m1 = useod && matchesByOD(input, ft);
				
				boolean m0 = ft.getItem() == input.getItem();
				boolean m2 = !usemeta || ft.getDamageValue() == input.getDamageValue();
				boolean m3 = ignorenbt || Objects.equals(ft.getComponents(), input.getComponents());
				
				if(m1 || (m0 && m2 && m3))
				{
					applies = true;
					break;
				}
			}
		}
		
		return invert != applies;
	}
	
	public static boolean matchesByOD(ItemStack a, ItemStack b)
	{
		if(a.isEmpty() && b.isEmpty())
			return true;
		if(a.isEmpty() || b.isEmpty())
			return false;
		Set<TagKey<Item>> tagsA = a.getItem().builtInRegistryHolder().tags().collect(Collectors.toSet());
		Set<TagKey<Item>> tagsB = b.getItem().builtInRegistryHolder().tags().collect(Collectors.toSet());
		return containsAny(tagsA, tagsB);
	}
	
	public static <T> boolean containsAny(Collection<T> coll1, Collection<T> coll2)
	{
		Iterator<T> it;
		T aColl1;
		if(coll1.size() < coll2.size())
		{
			it = coll1.iterator();
			while(it.hasNext())
			{
				aColl1 = it.next();
				if(coll2.contains(aColl1)) return true;
			}
		} else
		{
			it = coll2.iterator();
			while(it.hasNext())
			{
				aColl1 = it.next();
				if(coll1.contains(aColl1)) return true;
			}
		}
		return false;
	}
	
	public static boolean isFilterUpgrade(ItemStack stack)
	{
		return !stack.isEmpty() && stack.getItem() == ItemsSQ.UPGRADE_FILTER;
	}
	
	@Override
	public ItemStack handlePickup(ItemStack stack, TilePoweredQuarry quarry, int index)
	{
		if(!matches(quarry.getUpgradeStack(index), stack))
			return ItemStack.EMPTY;
		return stack;
	}
	
	@Override
	public boolean isCompatible(TilePoweredQuarry quarry)
	{
		return !hasUpgrade(quarry, this);
	}
}