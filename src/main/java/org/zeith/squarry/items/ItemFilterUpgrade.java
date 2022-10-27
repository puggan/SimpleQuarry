package org.zeith.squarry.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.zeith.squarry.blocks.entity.TilePoweredQuarry;
import org.zeith.squarry.init.ItemsSQ;
import org.zeith.squarry.inventory.ContainerFilter;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ItemFilterUpgrade
		extends ItemUpgrade
{
	public ItemFilterUpgrade()
	{
		super(new Properties().stacksTo(1));
		quarryUseMultiplier = 1;
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
		CompoundTag nbt = filter.getTag();

		if(nbt != null)
		{
			boolean invert = nbt.getBoolean("InvertList");
			boolean useod = nbt.contains("OreDictionary") && nbt.getBoolean("OreDictionary");
			boolean usemeta = !nbt.contains("Metadata") || nbt.getBoolean("Metadata");
			boolean ignorenbt = !nbt.contains("IgnoreNBT") || nbt.getBoolean("IgnoreNBT");

			boolean applies = false;

			ListTag list = nbt.getList("Filter", Tag.TAG_COMPOUND);

			for(int i = 0; i < list.size(); ++i)
			{
				ItemStack ft = ItemStack.of(list.getCompound(i));

				if(ft.isEmpty())
					continue;

				if(ft.getItem() == ItemsSQ.UPGRADE_FILTER && matches(ft, input))
				{
					applies = true;
					break;
				} else
				{
					boolean m1 = useod && matchesByOD(input, ft);

					boolean m0 = ft.getItem() == input.getItem();
					boolean m2 = !usemeta || ft.getDamageValue() == input.getDamageValue();
					boolean m3 = ignorenbt || Objects.equals(ft.getTag(), input.getTag());

					if(m1 || (m0 && m2 && m3))
					{
						applies = true;
						break;
					}
				}
			}

			return invert != applies;
		}

		return true;
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