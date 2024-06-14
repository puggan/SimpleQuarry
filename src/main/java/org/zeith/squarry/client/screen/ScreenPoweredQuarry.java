package org.zeith.squarry.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.zeith.hammerlib.client.screen.ScreenWTFMojang;
import org.zeith.hammerlib.client.utils.FXUtils;
import org.zeith.hammerlib.client.utils.RenderUtils;
import org.zeith.hammerlib.util.colors.ColorHelper;
import org.zeith.squarry.*;
import org.zeith.squarry.api.energy.UniversalConverter;
import org.zeith.squarry.blocks.entity.TilePoweredQuarry;
import org.zeith.squarry.inventory.ContainerPoweredQuarry;
import org.zeith.squarry.mixins.GuiGraphicsAccessor;

import java.text.DecimalFormat;

public class ScreenPoweredQuarry
		extends ScreenWTFMojang<ContainerPoweredQuarry>
{
	public static final ResourceLocation POWERED_QUARRY_TEXTURE = SimpleQuarry.id("textures/gui/powered_quarry.png");
	public TilePoweredQuarry tile;
	
	public ScreenPoweredQuarry(ContainerPoweredQuarry container, Inventory inv, Component label)
	{
		super(container, inv, label);
		this.tile = container.tile;
		setSize(176, 166);
	}
	
	@Override
	protected void renderBackground(GuiGraphics gfx, float partialTime, int mouseX, int mouseY)
	{
		gfx.blit(POWERED_QUARRY_TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		
		FXUtils.bindTexture(POWERED_QUARRY_TEXTURE);
		RenderUtils.drawTexturedModalRect(gfx, leftPos + 26.5F, topPos + 34, imageWidth, 14, 13, 13);
		
		if(tile.totalBurnTicks.getInt() != 0)
		{
			float fire = 1 + (float) tile.burnTicks.getInt() / tile.totalBurnTicks.getInt() * 13;
			RenderSystem.setShaderColor(1, 1, 1, 1);
			RenderUtils.drawTexturedModalRect(gfx, leftPos + 25, topPos + 47 - fire, imageWidth, 14 - fire, 14, fire);
		}
		
		float power = (float) (tile.storage.getStoredQF(null) / tile.storage.getQFCapacity(null) * 64);
		int finalCol = ColorHelper.interpolate(0xFF803400, 0xFFFE6A00, power / 64F);
		
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		RenderSystem.setShaderColor(1, 1, 1, 1);
		fillGradient(gfx, leftPos + 7, topPos + 72 - power, 11, power, 0, finalCol, 0xFF803400);
	}
	
	private static final DecimalFormat df = new DecimalFormat("#0");
	
	@Override
	protected boolean renderForeground(GuiGraphics gfx, int mx, int my)
	{
		if(mx - leftPos >= 6 && my - topPos >= 7 && mx - leftPos <= 19 && my - topPos <= 73)
		{
			RenderSystem.enableBlend();
			RenderSystem.setShader(GameRenderer::getPositionColorShader);
			RenderSystem.setShaderColor(1, 1, 1, 1);
			RenderUtils.drawColoredModalRect(gfx, 7, 8, 11, 64, 0x80FFFFFF);
			
			ItemStack mouse = menu.getCarried();
			if(mouse.isEmpty())
				gfx.renderTooltip(font, Component.literal(df.format(Math.floor(tile.storage.getStoredQF(null) / (UniversalConverter.FT_QF(SQCommonProxy.COAL.getBurnTime(null)) / SQConfig.getBlocksPerCoal()))) + " " + I18n.get("info.squarry.blockstobreak")), 16, 48);
		}
		
		return true;
	}
	
	public void fillGradient(GuiGraphics gfx, float x1, float y1, float width, float height, float z, int rgb1, int rgb2)
	{
		this.fillGradient(gfx, RenderType.gui(), x1, y1, x1 + width, y1 + height, z, rgb1, rgb2);
	}
	
	public void fillGradient(GuiGraphics gfx, RenderType type, float x1, float y1, float x2, float y2, float z, int rgb1, int rgb2)
	{
		VertexConsumer vertexconsumer = gfx.bufferSource().getBuffer(type);
		this.fillGradient(gfx, vertexconsumer, x1, y1, x2, y2, z, rgb1, rgb2);
		((GuiGraphicsAccessor) gfx).callFlushIfUnmanaged();
	}
	
	private void fillGradient(GuiGraphics gfx, VertexConsumer b, float x1, float y1, float x2, float y2, float z, int rgb1, int rgb2)
	{
		float f = (float) FastColor.ARGB32.alpha(rgb1) / 255.0F;
		float f1 = (float) FastColor.ARGB32.red(rgb1) / 255.0F;
		float f2 = (float) FastColor.ARGB32.green(rgb1) / 255.0F;
		float f3 = (float) FastColor.ARGB32.blue(rgb1) / 255.0F;
		float f4 = (float) FastColor.ARGB32.alpha(rgb2) / 255.0F;
		float f5 = (float) FastColor.ARGB32.red(rgb2) / 255.0F;
		float f6 = (float) FastColor.ARGB32.green(rgb2) / 255.0F;
		float f7 = (float) FastColor.ARGB32.blue(rgb2) / 255.0F;
		
		Matrix4f matrix4f = gfx.pose().last().pose();
		b.addVertex(matrix4f, x1, y1, z).setColor(f1, f2, f3, f);
		b.addVertex(matrix4f, x1, y2, z).setColor(f5, f6, f7, f4);
		b.addVertex(matrix4f, x2, y2, z).setColor(f5, f6, f7, f4);
		b.addVertex(matrix4f, x2, y1, z).setColor(f1, f2, f3, f);
	}
}