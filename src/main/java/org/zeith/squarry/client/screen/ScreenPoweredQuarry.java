package org.zeith.squarry.client.screen;

import com.mojang.blaze3d.shaders.Shader;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;
import org.zeith.hammerlib.client.screen.ScreenWTFMojang;
import org.zeith.hammerlib.client.utils.FXUtils;
import org.zeith.hammerlib.client.utils.RenderUtils;
import org.zeith.hammerlib.util.colors.ColorHelper;
import org.zeith.squarry.SQCommonProxy;
import org.zeith.squarry.SQConfig;
import org.zeith.squarry.SQConstants;
import org.zeith.squarry.api.energy.UniversalConverter;
import org.zeith.squarry.blocks.entity.TilePoweredQuarry;
import org.zeith.squarry.inventory.ContainerPoweredQuarry;

import java.text.DecimalFormat;
import java.util.List;

public class ScreenPoweredQuarry
		extends ScreenWTFMojang<ContainerPoweredQuarry>
{
	public TilePoweredQuarry tile;

	public ScreenPoweredQuarry(ContainerPoweredQuarry container, Inventory inv, Component label)
	{
		super(container, inv, label);
		this.tile = container.tile;
		setSize(176, 166);
	}

	@Override
	protected void renderBackground(PoseStack pose, float partialTime, int mouseX, int mouseY)
	{
		FXUtils.bindTexture(SQConstants.MOD_ID, "textures/gui/powered_quarry.png");
		RenderUtils.drawTexturedModalRect(leftPos, topPos, 0, 0, imageWidth, imageHeight);

		RenderUtils.drawTexturedModalRect(leftPos + 26.5, topPos + 34, imageWidth, 14, 13, 13);

		if(tile.totalBurnTicks.getInt() != 0)
		{
			double fire = 1 + (double) tile.burnTicks.getInt() / tile.totalBurnTicks.getInt() * 13;
			RenderSystem.setShaderColor(1, 1, 1, 1);
			RenderUtils.drawTexturedModalRect(leftPos + 25, topPos + 47 - fire, imageWidth, 14 - fire, 14, fire);
		}

		float power = (float) (tile.storage.getStoredQF(null) / tile.storage.getQFCapacity(null) * 64);
		int finalCol = ColorHelper.interpolate(0xFF803400, 0xFFFE6A00, power / 64F);

		RenderSystem.disableTexture();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderUtils.drawGradientRect(leftPos + 7, topPos + 72 - power, 11, power, finalCol, 0xFF803400);
		RenderSystem.enableTexture();
	}

	private static final DecimalFormat df = new DecimalFormat("#0");

	@Override
	protected boolean renderForeground(PoseStack matrix, int mx, int my)
	{
		if(mx - leftPos >= 6 && my - topPos >= 7 && mx - leftPos <= 19 && my - topPos <= 73)
		{
			RenderSystem.disableTexture();
			RenderUtils.drawColoredModalRect(matrix, leftPos + 7, topPos + 8, 11, 64, 0x80FFFFFF);
			RenderSystem.enableTexture();

			ItemStack mouse = menu.getCarried();
			if(mouse.isEmpty())
				renderTooltip(matrix, new TextComponent(df.format(Math.floor(tile.storage.getStoredQF(null) / (UniversalConverter.FT_QF(ForgeHooks.getBurnTime(SQCommonProxy.COAL, null)) / SQConfig.getBlockPerCoal()))) + " " + I18n.get("info.squarry.blockstobreak")), 16, 48);
		}

		return true;
	}
}