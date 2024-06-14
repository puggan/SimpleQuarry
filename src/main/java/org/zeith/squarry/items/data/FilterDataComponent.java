package org.zeith.squarry.items.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Builder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.UUID;

@Builder(toBuilder = true)
public record FilterDataComponent(
		boolean invertList, boolean useTags, boolean useDamage, boolean ignoreComponents,
		List<ItemStack> filter,
		UUID filterId
)
{
	public static final FilterDataComponent EMPTY = new FilterDataComponent(false, false, false, false, List.of(), new UUID(0, 0));
	
	public static final Codec<FilterDataComponent> CODEC = RecordCodecBuilder.create(inst ->
			inst.group(
					Codec.BOOL.fieldOf("invert").forGetter(FilterDataComponent::invertList),
					Codec.BOOL.fieldOf("tags").forGetter(FilterDataComponent::useTags),
					Codec.BOOL.fieldOf("damage").forGetter(FilterDataComponent::useDamage),
					Codec.BOOL.fieldOf("components").forGetter(FilterDataComponent::ignoreComponents),
					ItemStack.SINGLE_ITEM_CODEC.listOf().fieldOf("filter").forGetter(FilterDataComponent::filter),
					UUIDUtil.CODEC.fieldOf("id").forGetter(FilterDataComponent::filterId)
			).apply(inst, FilterDataComponent::new)
	);
	
	public static final StreamCodec<RegistryFriendlyByteBuf, FilterDataComponent> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.BOOL, FilterDataComponent::invertList,
			ByteBufCodecs.BOOL, FilterDataComponent::useTags,
			ByteBufCodecs.BOOL, FilterDataComponent::useDamage,
			ByteBufCodecs.BOOL, FilterDataComponent::ignoreComponents,
			ItemStack.LIST_STREAM_CODEC, FilterDataComponent::filter,
			UUIDUtil.STREAM_CODEC, FilterDataComponent::filterId,
			FilterDataComponent::new
	);
}