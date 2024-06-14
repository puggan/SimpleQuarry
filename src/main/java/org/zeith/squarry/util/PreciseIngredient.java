package org.zeith.squarry.util;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;

public class PreciseIngredient
{
	public static Ingredient enchantedBook(Holder<Enchantment> enchantment, int level)
	{
		ItemEnchantments.Mutable mut = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
		mut.set(enchantment, level);
		return DataComponentIngredient.of(
				true,
				DataComponentPredicate.builder()
						.expect(DataComponents.STORED_ENCHANTMENTS, mut.toImmutable())
						.build(),
				Items.ENCHANTED_BOOK
		);
	}
}
