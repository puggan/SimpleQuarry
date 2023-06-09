package org.zeith.squarry.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.zeith.hammerlib.client.screen.ScreenWTFMojang;
import org.zeith.hammerlib.client.utils.FXUtils;
import org.zeith.hammerlib.client.utils.RenderUtils;
import org.zeith.squarry.SQConstants;
import org.zeith.squarry.blocks.entity.TileFuelQuarry;
import org.zeith.squarry.inventory.ContainerFuelQuarry;

public class ScreenFuelQuarry
		extends ScreenWTFMojang<ContainerFuelQuarry>
{
	public TileFuelQuarry tile;
	
	public ScreenFuelQuarry(ContainerFuelQuarry container, Inventory inv, Component label)
	{
		super(container, inv, label);
		this.tile = container.tile;
		setSize(176, 166);
	}
	
	@Override
	protected void renderBackground(GuiGraphics gfx, float partialTime, int mouseX, int mouseY)
	{
		var pose = gfx.pose();
		
		FXUtils.bindTexture(SQConstants.MOD_ID, "textures/gui/fuel_quarry.png");
		RenderUtils.drawTexturedModalRect(gfx, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		
		RenderUtils.drawTexturedModalRect(gfx, leftPos + 81.5F, topPos + 34 - 6, imageWidth, 14, 13, 13);
		
		if(tile.totalBurnTicks.getInt() != 0)
		{
			float fire = 1 + (float) tile.burnTicks.getInt() / tile.totalBurnTicks.getInt() * 13;
			RenderSystem.setShaderColor(1, 1, 1, 1);
			RenderUtils.drawTexturedModalRect(gfx, leftPos + 81, topPos + 48 - 6 - fire, imageWidth, 14 - fire, 14, fire);
		}
	}
}