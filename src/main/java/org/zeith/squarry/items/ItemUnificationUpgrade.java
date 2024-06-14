package org.zeith.squarry.items;

import com.google.common.collect.*;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import org.zeith.squarry.SQConfig;
import org.zeith.squarry.SimpleQuarry;
import org.zeith.squarry.blocks.entity.TilePoweredQuarry;

import java.util.*;
import java.util.stream.Collectors;

@EventBusSubscriber
public class ItemUnificationUpgrade
		extends ItemUpgrade
{
	private static Multimap<TagKey<Item>, Item> UNIFIED_OBJECTS = ArrayListMultimap.create();
	private static Map<Item, Item> UNIFICATION_MAP = Map.of();
	
	public ItemUnificationUpgrade()
	{
		super(new Properties().stacksTo(1));
	}
	
	@Override
	public void handleDrops(TilePoweredQuarry quarry, BlockPos pos, NonNullList<ItemStack> drops)
	{
		if(!SQConfig.isEnableUnification()) return;
		drops.replaceAll(stack ->
		{
			var it = stack.getItem();
			var uit = UNIFICATION_MAP.get(it);
			if(uit != null) return new ItemStack(uit, stack.getCount());
			return stack;
		});
	}
	
	@SubscribeEvent
	public static void tagsRefresh(TagsUpdatedEvent e)
	{
		if(!e.shouldUpdateStaticData()) return;
		Registry<Item> items = e.getRegistryAccess().registryOrThrow(Registries.ITEM);
		
		Set<TagKey<Item>> excludedTags = Arrays.stream(SQConfig.getExcludedUnificationEntries())
				.filter(s -> s.startsWith("#"))
				.map(s -> s.substring(1))
				.map(ResourceLocation::tryParse)
				.filter(Objects::nonNull)
				.map(ItemTags::create)
				.collect(Collectors.toSet());
		
		Set<Item> excludedItems = Arrays.stream(SQConfig.getExcludedUnificationEntries())
				.filter(s -> !s.startsWith("#"))
				.map(ResourceLocation::tryParse)
				.filter(Objects::nonNull)
				.map(BuiltInRegistries.ITEM::get)
				.collect(Collectors.toSet());
		
		String[] materials = SQConfig.getAllowedUnificationMaterials();
		ArrayListMultimap<TagKey<Item>, Item> uniObjects = ArrayListMultimap.create();
		items.getTags()
				.filter(t -> canBeUnified(materials, t.getFirst()))
				.filter(t -> !excludedTags.contains(t.getFirst()))
				.map(Pair::getSecond)
				.forEach(tag -> uniObjects.putAll(tag.key(), tag.stream().map(Holder::value).filter(it -> !excludedItems.contains(it)).toList()));
		
		UNIFIED_OBJECTS = uniObjects;
		
		// Determine unification tags
		Multimap<Item, TagKey<Item>> tagsForItem = ArrayListMultimap.create(UNIFIED_OBJECTS.size(), 1);
		for(var entry : UNIFIED_OBJECTS.entries())
			tagsForItem.put(entry.getValue(), entry.getKey());
		
		// Remove dupes
		tagsForItem.asMap().entrySet().removeIf(c ->
		{
			Collection<TagKey<Item>> tags = c.getValue();
			if(tags.size() == 1) return false;
			
			Item item = c.getKey();
			
			SimpleQuarry.LOG.warn("Removed item {} from unification upgrade since it has multiple unification tags: {}",
					BuiltInRegistries.ITEM.getKey(item),
					tags.stream().map(TagKey::location).map(ResourceLocation::toString).collect(Collectors.joining(", ", "[", "]"))
			);
			
			for(TagKey<Item> tag : tags)
				UNIFIED_OBJECTS.remove(tag, item);
			
			return true;
		});
		
		ImmutableMap.Builder<Item, Item> uniMap = ImmutableMap.builder();
		
		for(Map.Entry<Item, TagKey<Item>> entry : tagsForItem.entries())
		{
			Collection<Item> cl = UNIFIED_OBJECTS.get(entry.getValue());
			if(cl.isEmpty()) continue;
			var result = cl.iterator().next();
			if(result == entry.getKey()) continue;
			uniMap.put(entry.getKey(), result);
		}
		
		UNIFICATION_MAP = uniMap.build();
		
		SimpleQuarry.LOG.info("Reloaded {} unification items with {} tags.", UNIFICATION_MAP.size(), UNIFIED_OBJECTS.keySet().size());
	}
	
	public static boolean canBeUnified(String[] materials, TagKey<Item> tag)
	{
		var l = tag.location().toString();
		for(String string : materials)
			if((string.endsWith("/") && l.startsWith(string)) || l.equals(string))
				return true;
		return false;
	}
}