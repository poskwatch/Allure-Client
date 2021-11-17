package vip.allureclient.base.util.visual;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public class RenderUtil {

    public static void glFilledQuad(float x, float y, float width, float height, int color) {
        //TODO: Add actual GL filled quad
        Gui.drawRectWithWidth(x, y, width, height, color);
    }

    public static void startScissorBox(ScaledResolution sr, int x, int y, int width, int height) {
        int sf = sr.getScaleFactor();
        GL11.glScissor(x, (sr.getScaledHeight() - y + height),width,height);
    }
}
