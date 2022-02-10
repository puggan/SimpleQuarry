package org.zeith.squarry.init;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.common.Tags;
import org.zeith.hammerlib.annotations.ProvideRecipes;
import org.zeith.hammerlib.api.IRecipeProvider;
import org.zeith.hammerlib.event.recipe.RegisterRecipesEvent;
import org.zeith.squarry.SQConfig;
import org.zeith.squarry.blocks.BlockFuelQuarry;
import org.zeith.squarry.blocks.BlockPoweredQuarry;
import org.zeith.squarry.util.PreciseIngredient;

import java.util.Map;

@ProvideRecipes
public class RecipesSQ
		implements IRecipeProvider
{
	public static ItemStack enchantedBook(Enchantment ench, int lvl)
	{
		ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
		EnchantmentHelper.setEnchantments(Map.of(ench, lvl), book);
		return book;
	}

	@Override
	public void provideRecipes(RegisterRecipesEvent event)
	{
		SQConfig.reload();

		ItemStack topQuarry = new ItemStack(BlockFuelQuarry.FUEL_QUARRY);
		ItemStack silkTouchBook = enchantedBook(Enchantments.SILK_TOUCH, 1);
		ItemStack fortune1Book = enchantedBook(Enchantments.BLOCK_FORTUNE, 1);
		ItemStack fortune2Book = enchantedBook(Enchantments.BLOCK_FORTUNE, 2);
		ItemStack fortune3Book = enchantedBook(Enchantments.BLOCK_FORTUNE, 3);

		event.shaped()
				.result(new ItemStack(BlockFuelQuarry.FUEL_QUARRY))
				.id(BlockFuelQuarry.FUEL_QUARRY.getRegistryName())
				.shape("pip", "fgf", "pdp")
				.map('p', Tags.Items.ENDER_PEARLS)
				.map('i', Items.IRON_PICKAXE)
				.map('f', Items.FURNACE)
				.map('g', Items.CLOCK)
				.map('d', Items.DIAMOND_PICKAXE)
				.registerIf(SQConfig::enableFuelQuarry);

		if(SQConfig.enableUpgrades())
		{
			event.shaped()
					.result(new ItemStack(ItemsSQ.UPGRADE_BASE))
					.id(ItemsSQ.UPGRADE_BASE.getRegistryName())
					.shape("rir", "igi", "rir")
					.map('r', Tags.Items.DUSTS_REDSTONE)
					.map('i', Tags.Items.INGOTS_IRON)
					.map('g', Tags.Items.STONE)
					.register();

			event.shaped()
					.result(new ItemStack(ItemsSQ.UPGRADE_FILTER))
					.id(ItemsSQ.UPGRADE_FILTER.getRegistryName())
					.shape("rsr", "sus", "rsr")
					.map('r', Tags.Items.DUSTS_REDSTONE)
					.map('s', Tags.Items.STRING)
					.map('u', ItemsSQ.UPGRADE_BASE)
					.register();

			event.shaped()
					.result(new ItemStack(ItemsSQ.UPGRADE_SILK))
					.id(ItemsSQ.UPGRADE_SILK.getRegistryName())
					.shape("lbl", "puh", "lel")
					.map('l', Items.SEA_LANTERN)
					.map('b', new PreciseIngredient(silkTouchBook))
					.map('p', Items.GOLDEN_PICKAXE)
					.map('u', ItemsSQ.UPGRADE_BASE)
					.map('h', Items.GOLDEN_SHOVEL)
					.map('e', Tags.Items.GEMS_EMERALD)
					.register();

//			event.shaped()
//					.result(new ItemStack(ItemsSQ.UPGRADE_UNIFICATION))
//					.id(ItemsSQ.UPGRADE_UNIFICATION.getRegistryName())
//					.shape("beb", "gug", "bgb")
//					.map('g', Items.BOOKSHELF)
//					.map('e', Items.ENCHANTING_TABLE)
//					.map('b', Tags.Items.INGOTS_GOLD)
//					.map('u', ItemsSQ.UPGRADE_BASE)
//					.register();

			event.shaped()
					.result(new ItemStack(ItemsSQ.UPGRADE_AUTO_SMELT))
					.id(ItemsSQ.UPGRADE_AUTO_SMELT.getRegistryName())
					.shape("olo", "lul", "olo")
					.map('o', Tags.Items.OBSIDIAN)
					.map('l', Items.LAVA_BUCKET)
					.map('u', ItemsSQ.UPGRADE_BASE)
					.register();

			event.shaped()
					.result(new ItemStack(ItemsSQ.UPGRADE_FILLER))
					.id(ItemsSQ.UPGRADE_FILLER.getRegistryName())
					.shape("mdm", "dud", "mdm")
					.map('m', Items.DIAMOND_SHOVEL)
					.map('d', Items.GRASS)
					.map('u', ItemsSQ.UPGRADE_BASE)
					.register();

			event.shaped()
					.result(new ItemStack(ItemsSQ.UPGRADE_FORTUNE1))
					.id(ItemsSQ.UPGRADE_FORTUNE1.getRegistryName())
					.shape("ibi", "dud", "idi")
					.map('i', Tags.Items.INGOTS_IRON)
					.map('b', new PreciseIngredient(fortune1Book))
					.map('d', Tags.Items.GEMS_DIAMOND)
					.map('u', ItemsSQ.UPGRADE_BASE)
					.register();

			event.shaped()
					.result(new ItemStack(ItemsSQ.UPGRADE_FORTUNE2))
					.id(ItemsSQ.UPGRADE_FORTUNE2.getRegistryName())
					.shape("ibi", "dud", "idi")
					.map('i', Tags.Items.INGOTS_GOLD)
					.map('b', new PreciseIngredient(fortune2Book))
					.map('d', Tags.Items.GEMS_EMERALD)
					.map('u', ItemsSQ.UPGRADE_BASE)
					.register();

			event.shaped()
					.result(new ItemStack(ItemsSQ.UPGRADE_FORTUNE3))
					.id(ItemsSQ.UPGRADE_FORTUNE3.getRegistryName())
					.shape("ibi", "dud", "idi")
					.map('i', Tags.Items.GEMS_AMETHYST)
					.map('b', new PreciseIngredient(fortune3Book))
					.map('d', Tags.Items.INGOTS_NETHERITE)
					.map('u', ItemsSQ.UPGRADE_BASE)
					.register();

			event.shaped()
					.result(new ItemStack(ItemsSQ.UPGRADE_EFFICIENCY1))
					.id(ItemsSQ.UPGRADE_EFFICIENCY1.getRegistryName())
					.shape("iri", "rur", "iri")
					.map('i', Tags.Items.INGOTS_IRON)
					.map('r', Tags.Items.STORAGE_BLOCKS_REDSTONE)
					.map('u', ItemsSQ.UPGRADE_BASE)
					.register();

			event.shaped()
					.result(new ItemStack(ItemsSQ.UPGRADE_EFFICIENCY2))
					.id(ItemsSQ.UPGRADE_EFFICIENCY2.getRegistryName())
					.shape("ibi", "rur", "ibi")
					.map('i', Tags.Items.INGOTS_GOLD)
					.map('r', Tags.Items.GEMS_AMETHYST)
					.map('u', ItemsSQ.UPGRADE_BASE)
					.map('b', Tags.Items.GEMS_DIAMOND)
					.register();

			event.shaped()
					.result(new ItemStack(ItemsSQ.UPGRADE_EFFICIENCY3))
					.id(ItemsSQ.UPGRADE_EFFICIENCY3.getRegistryName())
					.shape("ibi", "rur", "ibi")
					.map('i', Tags.Items.INGOTS_GOLD)
					.map('r', Tags.Items.GEMS_DIAMOND)
					.map('u', ItemsSQ.UPGRADE_BASE)
					.map('b', Tags.Items.GEMS_EMERALD)
					.register();
		}

		if(SQConfig.enablePoweredQuarry())
		{
			topQuarry = new ItemStack(BlockPoweredQuarry.POWERED_QUARRY);

			if(SQConfig.easyPoweredQuarryRecipe())
				event.shaped()
						.result(new ItemStack(BlockPoweredQuarry.POWERED_QUARRY))
						.id(BlockPoweredQuarry.POWERED_QUARRY.getRegistryName())
						.shape("ehe", "dqd", "ece")
						.map('c', Tags.Items.CHESTS_WOODEN)
						.map('q', BlockFuelQuarry.FUEL_QUARRY)
						.map('h', Items.HOPPER)
						.map('e', Items.ENDER_EYE)
						.map('d', Items.DIAMOND_PICKAXE)
						.register();
			else
				event.shaped()
						.result(new ItemStack(BlockPoweredQuarry.POWERED_QUARRY))
						.id(BlockPoweredQuarry.POWERED_QUARRY.getRegistryName())
						.shape("phl", "dqd", "sem")
						.map('q', BlockFuelQuarry.FUEL_QUARRY)
						.map('d', Items.DIAMOND_PICKAXE)
						.map('p', Items.PODZOL)
						.map('h', Items.HOPPER)
						.map('l', Items.SEA_LANTERN)
						.map('s', Items.AMETHYST_BLOCK)
						.map('e', Items.ENDER_CHEST)
						.map('m', Items.ANCIENT_DEBRIS)
						.register();
		}
	}
}
