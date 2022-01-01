package vip.allureclient.base.util.visual;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.base.util.math.MathUtil;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class GLUtil {

    public static void glFilledQuad(double x, double y, double width, double height, int color) {
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(770, 771);
        glColor(color);
        glBegin(GL_QUADS);
        {
            glVertex2d(x, y);
            glVertex2d(x, y + height);
            glVertex2d(x + width, y + height);
            glVertex2d(x + width, y);
        }
        glEnd();
        glEnable(GL_TEXTURE_2D);
    }

    public static void glOutlinedFilledQuad(double x, double y, double width, double height, int quadColor, int outlineColor) {
        glFilledQuad(x + 0.5, y + 0.5, width - 1, height - 1, quadColor);
        glLine(x, y, x, y + height, 1.5F, outlineColor);
        glLine(x + width, y, x + width, y + height, 1.5F, outlineColor);
        glLine(x, y, x + width, y, 1.5F, outlineColor);
        glLine(x, y + height, x + width, y + height, 1.5F, outlineColor);
    }

    public static void glLine(double startX, double startY, double endX, double endY, float lineWidth, int color) {
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(770, 771);
        glColor(color);
        glLineWidth(lineWidth);
        glBegin(GL_LINES);
        {
            glVertex2d(startX, startY);
            glVertex2d(endX, endY);
        }
        glEnd();
        glEnable(GL_TEXTURE_2D);
    }

    public static void glHorizontalGradientQuad(double x, double y, double width, double height, int startColor, int endColor) {
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(770, 771);
        glEnable(GL_LINE_SMOOTH);
        glShadeModel(GL_SMOOTH);
        glBegin(GL_QUADS);
        {
            glColor(startColor);
            glVertex2d(x, y);
            glVertex2d(x, y + height);
            glColor(endColor);
            glVertex2d(x + width, y + height);
            glVertex2d(x + width, y);
        }
        glEnd();
        glShadeModel(GL_FLAT);
        glEnable(GL_TEXTURE_2D);
    }

    public static void glVerticalGradientQuad(double x, double y, double width, double height, int startColor, int endColor) {
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(770, 771);
        glEnable(GL_LINE_SMOOTH);
        glShadeModel(GL_SMOOTH);
        glBegin(GL_QUADS);
        {
            glColor(startColor);
            glVertex2d(x + width, y);
            glVertex2d(x, y);
            glColor(endColor);
            glVertex2d(x, y + height);
            glVertex2d(x + width, y + height);
        }
        glEnd();
        glShadeModel(GL_FLAT);
        glEnable(GL_TEXTURE_2D);
    }

    public static void glFilledEllipse(double x, double y, float radius, int color) {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_POINT_SMOOTH);
        glHint(GL_POINT_SMOOTH_HINT, GL_NICEST);
        glDisable(GL_TEXTURE_2D);
        glColor(color);
        glPointSize(radius);
        glBegin(GL_POINTS);
        {
            glVertex2d(x, y);
        }
        glEnd();
        glDisable(GL_POINT_SMOOTH);
        glHint(GL_POINT_SMOOTH_HINT, GL_DONT_CARE);
        glEnable(GL_TEXTURE_2D);
    }

    public static void gl3DEllipse(Entity entity, float partialTicks, float points, float radius, int color) {
        glPushMatrix();
        GlStateManager.enableDepth();
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_LINE_SMOOTH);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glLineWidth(3.0F);
        glBegin(GL_LINE_LOOP);
        {
            final double x = MathUtil.linearInterpolate(entity.lastTickPosX, entity.posX, partialTicks) - Wrapper.getRenderManager().viewerPosX,
                    y = MathUtil.linearInterpolate(entity.lastTickPosY, entity.posY, partialTicks) - Wrapper.getRenderManager().viewerPosY,
                    z = MathUtil.linearInterpolate(entity.lastTickPosZ, entity.posZ, partialTicks) - Wrapper.getRenderManager().viewerPosZ;
            for (int i = 0; i <= points; i++) {
                    glColor(ColorUtil.interpolateColorsDynamic(3, i * 10, Color.MAGENTA, Color.MAGENTA.darker().darker().darker().darker().darker()).getRGB());
                    glVertex3d(x + radius * Math.cos(i * Math.PI / (points / 2)), y + 0.5, z + radius * Math.sin(i * Math.PI / (points / 2)));
            }
        }
        glEnd();
        glDepthMask(true);
        glEnable(GL_DEPTH_TEST);
        glDisable(GL_LINE_SMOOTH);
        glEnable(GL_TEXTURE_2D);
        glPopMatrix();
    }

    public static void glStartScissor() {
        glClear(GL_DEPTH_BUFFER_BIT);
        glEnable(GL_SCISSOR_TEST);
    }

    public static void glEndScissor() {
        Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(false);
        glDisable(GL_SCISSOR_TEST);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ZERO, GL_ONE);
        GlStateManager.disableBlend();
    }

    public static void glScissor(double x, double y, double width, double height) {
        int scaleFactor = new ScaledResolution(Wrapper.getMinecraft()).getScaleFactor();
        while (scaleFactor < 2 && Minecraft.getMinecraft().displayWidth / (scaleFactor + 1) >= 320  && Minecraft.getMinecraft().displayHeight / (scaleFactor + 1) >= 240) {
            ++scaleFactor;
        }
        GL11.glScissor((int) (x * scaleFactor), (int) (Minecraft.getMinecraft().displayHeight - (y + height) * scaleFactor), (int) (width * scaleFactor), (int) (height * scaleFactor));
    }

    public static void glColor(final int color) {
        glColor4ub((byte)(color >> 16 & 0xFF), (byte)(color >> 8 & 0xFF), (byte)(color & 0xFF), (byte)(color >> 24 & 0xFF));
    }
}
