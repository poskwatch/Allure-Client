package vip.allureclient.base.util.visual;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
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
                glColor(ColorUtil.interpolateColorsDynamic(3, i * 15, Color.MAGENTA, Color.MAGENTA.darker().darker().darker().darker().darker()).getRGB());
                glVertex3d(x + radius * Math.cos(i * Math.PI / (points/2)), y + 0.5, z + radius * Math.sin(i * Math.PI / (points/2)));
            }
        }
        glEnd();
        glDepthMask(true);
        glEnable(GL_DEPTH_TEST);
        glDisable(GL_LINE_SMOOTH);
        glEnable(GL_TEXTURE_2D);
        glPopMatrix();
    }

    public static void glColor(final int color) {
        glColor4ub((byte)(color >> 16 & 0xFF), (byte)(color >> 8 & 0xFF), (byte)(color & 0xFF), (byte)(color >> 24 & 0xFF));
    }
}
