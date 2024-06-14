package org.zeith.squarry.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.zeith.hammerlib.api.proxy.IClientProxy;
import org.zeith.squarry.SQCommonProxy;
import org.zeith.squarry.SQConfig;
import org.zeith.squarry.api.energy.UniversalConverter;
import org.zeith.squarry.api.particle.ClientQuarryVortex;
import org.zeith.squarry.api.particle.ParticleVortex;
import org.zeith.squarry.blocks.entity.TileFuelQuarry;
import org.zeith.squarry.client.screen.ScreenFilter;
import org.zeith.squarry.inventory.*;
import org.zeith.squarry.items.ItemUpgrade;

public class SQClientProxy
		extends SQCommonProxy
		implements IClientProxy
{
	@Override
	public void setup(IEventBus modBus)
	{
		super.setup(modBus);
		modBus.addListener(this::clientSetup);
		NeoForge.EVENT_BUS.addListener(this::tooltip);
	}
	
	private void clientSetup(RegisterMenuScreensEvent e)
	{
		e.register(ContainerFilter.FILTER, ScreenFilter::new);
	}
	
	@Override
	public ParticleVortex createQuarryVortex(TileFuelQuarry quarry)
	{
		return new ClientQuarryVortex(quarry);
	}
	
	private void tooltip(ItemTooltipEvent e)
	{
		Player p = e.getEntity();
		if(p == null)
			return;
		
		ItemStack it = e.getItemStack();
		
		if(!it.isEmpty() && it.getItem() instanceof ItemUpgrade up)
		{
			e.getToolTip().add(Component.literal(I18n.get("info.squarry.fuel_use_boost", Math.round(up.quarryUseMultiplierClient * 1000F) / 1000F)).withStyle(ChatFormatting.DARK_PURPLE));
		}
		
		if(p.containerMenu instanceof ContainerFuelQuarry || p.containerMenu instanceof ContainerPoweredQuarry)
		{
			TileFuelQuarry quarry = null;
			
			if(p.containerMenu instanceof ContainerFuelQuarry m)
				quarry = m.tile;
			
			if(p.containerMenu instanceof ContainerPoweredQuarry m)
				quarry = m.tile;
			
			int burnTime = e.getItemStack().getBurnTime(null);
			
			if(burnTime > 0)
			{
				float mod = quarry != null ? (float) quarry.getUsageMult() : 1F;
				e.getToolTip().add(Component.translatable("info.squarry.blocks_broken").withStyle(ChatFormatting.DARK_GRAY).append(": " + (int) (UniversalConverter.FT_QF(burnTime / mod) / (UniversalConverter.FT_QF(COAL.getBurnTime(null)) / SQConfig.getBlocksPerCoal()))));
				e.getToolTip().add(Component.translatable("info.squarry.qfuel_use_boost").withStyle(ChatFormatting.DARK_GRAY).append(" " + (int) (mod * 100) + "%"));
			} else
				e.getToolTip().add(Component.translatable("info.squarry.not_fuel").withStyle(ChatFormatting.DARK_GRAY));
		}
	}
}