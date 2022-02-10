package org.zeith.squarry.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.zeith.squarry.SQCommonProxy;
import org.zeith.squarry.SQConfig;
import org.zeith.squarry.api.energy.UniversalConverter;
import org.zeith.squarry.blocks.entity.TileFuelQuarry;
import org.zeith.squarry.client.screen.ScreenFilter;
import org.zeith.squarry.inventory.ContainerFilter;
import org.zeith.squarry.inventory.ContainerFuelQuarry;
import org.zeith.squarry.inventory.ContainerPoweredQuarry;
import org.zeith.squarry.items.ItemUpgrade;

public class SQClientProxy
		extends SQCommonProxy
{
	{
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
		MinecraftForge.EVENT_BUS.addListener(this::tooltip);
	}

	private void clientSetup(FMLClientSetupEvent e)
	{
		MenuScreens.register(ContainerFilter.FILTER, ScreenFilter::new);
	}

	private void tooltip(ItemTooltipEvent e)
	{
		Player p = e.getPlayer();
		if(p == null)
			return;

		ItemStack it = e.getItemStack();

		if(!it.isEmpty() && it.getItem() instanceof ItemUpgrade up)
		{
			e.getToolTip().add(new TextComponent(I18n.get("info.squarry.fuel_use_boost", Math.round(up.quarryUseMultiplierClient * 1000F) / 1000F)).withStyle(ChatFormatting.DARK_PURPLE));
		}

		if(p.containerMenu instanceof ContainerFuelQuarry || p.containerMenu instanceof ContainerPoweredQuarry)
		{
			TileFuelQuarry quarry = null;

			if(p.containerMenu instanceof ContainerFuelQuarry m)
				quarry = m.tile;

			if(p.containerMenu instanceof ContainerPoweredQuarry m)
				quarry = m.tile;

			int burnTime = ForgeHooks.getBurnTime(e.getItemStack(), null);

			if(burnTime > 0)
			{
				float mod = quarry != null ? (float) quarry.getUsageMult() : 1F;
				e.getToolTip().add(new TranslatableComponent("info.squarry.blocks_broken").withStyle(ChatFormatting.DARK_GRAY).append(": " + (int) (UniversalConverter.FT_QF(burnTime / mod) / (UniversalConverter.FT_QF(ForgeHooks.getBurnTime(COAL, null)) / SQConfig.getBlockPerCoal()))));
				e.getToolTip().add(new TranslatableComponent("info.squarry.qfuel_use_boost").withStyle(ChatFormatting.DARK_GRAY).append(" " + (int) (mod * 100) + "%"));
			} else
				e.getToolTip().add(new TranslatableComponent("info.squarry.not_fuel").withStyle(ChatFormatting.DARK_GRAY));
		}
	}
}