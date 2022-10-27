package org.zeith.squarry.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import org.zeith.hammerlib.client.screen.ScreenWTFMojang;
import org.zeith.hammerlib.client.utils.FXUtils;
import org.zeith.hammerlib.client.utils.RenderUtils;
import org.zeith.squarry.SQConstants;
import org.zeith.squarry.inventory.ContainerFilter;

public class ScreenFilter
		extends ScreenWTFMojang<ContainerFilter>
{
	public ScreenFilter(ContainerFilter container, Inventory plyerInv, Component name)
	{
		super(container, plyerInv, name);
	}

	@Override
	protected void renderBackground(PoseStack pose, float partialTime, int mouseX, int mouseY)
	{
		FXUtils.bindTexture(SQConstants.MOD_ID, "textures/gui/filter.png");
		RenderUtils.drawTexturedModalRect(leftPos, topPos, 0, 0, imageWidth, imageHeight);

		ContainerFilter.FilterData filter = menu.data;

		FXUtils.bindTexture(SQConstants.MOD_ID, "textures/gui/widgets.png");

		{
			boolean hover = mouseX >= leftPos + 18 && mouseY >= topPos + 17 && mouseX < leftPos + 34 && mouseY < topPos + 33;

			RenderUtils.drawTexturedModalRect(pose, leftPos + 18, topPos + 17, 13 + (hover ? 16 : 0), 16 + (filter.invert ? 0 : 16), 16, 16);
		}

		{
			boolean hover = mouseX >= leftPos + 18 && mouseY >= topPos + 17 + 18 && mouseX < leftPos + 34 && mouseY < topPos + 33 + 18;

			RenderUtils.drawTexturedModalRect(pose, leftPos + 18, topPos + 35, 13 + (hover ? 16 : 0), 48 + (filter.useod ? 0 : 16), 16, 16);
		}

		{
			boolean hover = mouseX >= leftPos + 18 && mouseY >= topPos + 17 + 36 && mouseX < leftPos + 34 && mouseY < topPos + 33 + 36;

			RenderUtils.drawTexturedModalRect(pose, leftPos + 18, topPos + 53, 13 + (hover ? 16 : 0), 80 + (filter.usemeta ? 0 : 16), 16, 16);
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int p_97750_)
	{
		if(mouseX >= leftPos + 18 && mouseY >= topPos + 17 && mouseX < leftPos + 34 && mouseY < topPos + 33 && handleClick(0))
		{
			minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1));
			return true;
		}

		if(mouseX >= leftPos + 18 && mouseY >= topPos + 17 + 18 && mouseX < leftPos + 34 && mouseY < topPos + 33 + 18 && handleClick(1))
		{
			minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1));
			return true;
		}

		if(mouseX >= leftPos + 18 && mouseY >= topPos + 17 + 36 && mouseX < leftPos + 34 && mouseY < topPos + 33 + 36 && handleClick(2))
		{
			minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1));
			return true;
		}

		return super.mouseClicked(mouseX, mouseY, p_97750_);
	}

	public boolean handleClick(int k)
	{
		if(this.menu.clickMenuButton(this.minecraft.player, k))
		{
			this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, k);
			return true;
		}
		return false;
	}

	@Override
	protected boolean renderForeground(PoseStack matrix, int mouseX, int mouseY)
	{
		ContainerFilter.FilterData filter = menu.data;

		{
			boolean hover = mouseX >= leftPos + 18 && mouseY >= topPos + 17 && mouseX < leftPos + 34 && mouseY < topPos + 33;
			if(hover)
				renderTooltip(matrix, Component.translatable("info.squarry.filter." + (filter.invert ? "blacklist" : "whitelist")), mouseX - leftPos, mouseY - topPos);
		}

		{
			boolean hover = mouseX >= leftPos + 18 && mouseY >= topPos + 17 + 18 && mouseX < leftPos + 34 && mouseY < topPos + 33 + 18;
			if(hover)
				renderTooltip(matrix, Component.translatable("info.squarry.filter.oredict." + (filter.useod ? "yes" : "no")), mouseX - leftPos, mouseY - topPos);
		}

		{
			boolean hover = mouseX >= leftPos + 18 && mouseY >= topPos + 17 + 36 && mouseX < leftPos + 34 && mouseY < topPos + 33 + 36;
			if(hover)
				renderTooltip(matrix, Component.translatable("info.squarry.filter.meta." + (filter.usemeta ? "yes" : "no")), mouseX - leftPos, mouseY - topPos);
		}

		return true;
	}
}