package org.zeith.squarry.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.zeith.hammerlib.client.screen.ScreenWTFMojang;
import org.zeith.hammerlib.client.utils.FXUtils;
import org.zeith.hammerlib.client.utils.RenderUtils;
import org.zeith.hammerlib.util.colors.ColorHelper;
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
	protected void renderBackground(PoseStack pose, float partialTime, int mouseX, int mouseY)
	{
		FXUtils.bindTexture(SQConstants.MOD_ID, "textures/gui/fuel_quarry.png");
		RenderUtils.drawTexturedModalRect(leftPos, topPos, 0, 0, imageWidth, imageHeight);

		RenderUtils.drawTexturedModalRect(leftPos + 81.5, topPos + 34 - 6, imageWidth, 14, 13, 13);

		if(tile.totalBurnTicks.getInt() != 0)
		{
			double fire = 1 + (double) tile.burnTicks.getInt() / tile.totalBurnTicks.getInt() * 13;
			RenderSystem.setShaderColor(1, 1, 1, 1);
			RenderUtils.drawTexturedModalRect(leftPos + 81, topPos + 48 - 6 - fire, imageWidth, 14 - fire, 14, fire);
		}
	}
}