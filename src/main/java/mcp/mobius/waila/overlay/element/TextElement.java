package mcp.mobius.waila.overlay.element;

import com.mojang.blaze3d.matrix.MatrixStack;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.ui.Element;
import mcp.mobius.waila.api.ui.Size;
import mcp.mobius.waila.impl.config.WailaConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TextElement extends Element {

	public final ITextComponent component;

	public TextElement(ITextComponent component) {
		this.component = component;
	}

	@Override
	public Size getSize() {
		FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
		return new Size(fontRenderer.getStringWidth(component.getString()), fontRenderer.FONT_HEIGHT + 1);
	}

	@Override
	public void render(MatrixStack matrixStack, int x, int y, int maxX, int maxY) {
		FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
		WailaConfig.ConfigOverlay.ConfigOverlayColor color = Waila.CONFIG.get().getOverlay().getColor();
		IRenderTypeBuffer.Impl irendertypebuffer$impl = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
		fontRenderer.drawEntityText(component.func_241878_f(), x, y, color.getFontColor(), true, matrixStack.getLast().getMatrix(), irendertypebuffer$impl, false, 0, 15728880);
		irendertypebuffer$impl.finish();
	}

}
