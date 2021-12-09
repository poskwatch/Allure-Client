package vip.allureclient.base.util.visual;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.base.util.math.MathUtil;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.glBlendEquation;

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
        glEnable(GL_DEPTH);
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_LINE_SMOOTH);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glBegin(GL_LINE_LOOP);

        final double x = MathUtil.linearInterpolate(entity.lastTickPosX, entity.posX, partialTicks) - Wrapper.getRenderManager().getRenderPosX(),
                y = MathUtil.linearInterpolate(entity.lastTickPosY, entity.posY, partialTicks) - Wrapper.getRenderManager().getRenderPosY(),
                z = MathUtil.linearInterpolate(entity.lastTickPosZ, entity.posZ, partialTicks) - Wrapper.getRenderManager().getRenderPosZ();
        for (int i = 0; i <= points; i++) {
            glVertex3d(x + (radius * Math.cos((i * (Math.PI * 2) / points))), y, z);
        }

        glDisable(GL_DEPTH);
    }

    public static void prepareScissorBox(int x, int y, int width, int height, ScaledResolution sr) {
        int sf = sr.getScaleFactor();
        GL11.glScissor(x * 2, (sr.getScaledHeight() - y + height) * 2,width * 2, height * 2);
    }

    public static void glColor(final int color) {
        glColor4ub((byte)(color >> 16 & 0xFF), (byte)(color >> 8 & 0xFF), (byte)(color & 0xFF), (byte)(color >> 24 & 0xFF));
    }
}
