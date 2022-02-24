package vip.allureclient.base.util.visual.glsl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.GL11;
import vip.allureclient.base.util.math.MathUtil;
import vip.allureclient.base.util.visual.glsl.impl.ShaderRepository;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class GLUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void drawFilledRectangle(double x, double y, double width, double height, int color) {
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

    // Shader implementation for rounded rectangle

    public static void glRoundedFilledQuad(double x, double y, float width, float height, float radius, int color) {

        glEnable(GL_BLEND);
        final GLShader roundedQuadShader = ShaderRepository.ROUNDED_QUAD_SHADER;
        glUseProgram(roundedQuadShader.getProgram());
        glUniform1f(roundedQuadShader.getUniformLocation("width"), width);
        glUniform1f(roundedQuadShader.getUniformLocation("height"), height);
        glUniform1f(roundedQuadShader.getUniformLocation("radius"), radius);
        glUniform4f(roundedQuadShader.getUniformLocation("colour"),
                (color >> 16 & 0xFF) / 255.f,
                (color >> 8 & 0xFF) / 255.f,
                (color & 0xFF) / 255.f,
                (color >> 24 & 0xFF) / 255.f);

        glDisable(GL_TEXTURE_2D);

        glBegin(GL_QUADS);
        {
            glTexCoord2f(0.f, 0.f);
            glVertex2d(x, y);

            glTexCoord2f(0.f, 1.f);
            glVertex2d(x, y + height);

            glTexCoord2f(1.f, 1.f);
            glVertex2d(x + width, y + height);

            glTexCoord2f(1.f, 0.f);
            glVertex2d(x + width, y);
        }
        glEnd();

        glUseProgram(0);

        glEnable(GL_TEXTURE_2D);
    }

    public static void glFilledTriangle(Vec2d vector1, Vec2d vector2, Vec2d vector3, int color) {
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(770, 771);
        glColor(color);
        glBegin(GL_TRIANGLES);
        {
            glVertex2d(vector1.x, vector1.y);
            glVertex2d(vector2.x, vector2.y);
            glVertex2d(vector3.x, vector3.y);
        }
        glEnd();
        glEnable(GL_TEXTURE_2D);
    }

    public static void glFilledEquilateralTriangle(double x, double y, double length, boolean inverted, int color) {
        if (inverted)
            glFilledTriangle(new Vec2d(x, y), new Vec2d(x - length, y - length), new Vec2d(x + length, y - length), color);
        else
            glFilledTriangle(new Vec2d(x, y), new Vec2d(x - length, y + length), new Vec2d(x + length, y + length), color);
    }

    public static void glOutlinedFilledQuad(double x, double y, double width, double height, int quadColor, int outlineColor) {
        drawFilledRectangle(x + 0.5, y + 0.5, width - 1, height - 1, quadColor);
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
            final double x = MathUtil.linearInterpolate(entity.lastTickPosX, entity.posX, partialTicks) - mc.getRenderManager().viewerPosX,
                    y = MathUtil.linearInterpolate(entity.lastTickPosY, entity.posY, partialTicks) - mc.getRenderManager().viewerPosY,
                    z = MathUtil.linearInterpolate(entity.lastTickPosZ, entity.posZ, partialTicks) - mc.getRenderManager().viewerPosZ;
            for (int i = 0; i <= points; i++) {
                    glColor(color);
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

    public static void glAxisAlignedBBQuad(AxisAlignedBB axisAlignedBB, int color) {
        glPushMatrix();
        GlStateManager.enableDepth();
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_LINE_SMOOTH);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_QUADS);
        glColor(color);
        glVertex2d(axisAlignedBB.minX, axisAlignedBB.minY);
        glVertex2d(axisAlignedBB.maxX, axisAlignedBB.minY);
        glVertex2d(axisAlignedBB.maxX, axisAlignedBB.maxY);
        glVertex2d(axisAlignedBB.minX, axisAlignedBB.maxY);
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
        // Why is this so fucking unreadable
        int scaleFactor = new ScaledResolution(mc).getScaleFactor();
        while (scaleFactor < 2 && Minecraft.getMinecraft().displayWidth / (scaleFactor + 1) >= 320  && Minecraft.getMinecraft().displayHeight / (scaleFactor + 1) >= 240) {
            ++scaleFactor;
        }
        GL11.glScissor((int) (x * scaleFactor), (int) (Minecraft.getMinecraft().displayHeight - (y + height) * scaleFactor), (int) (width * scaleFactor), (int) (height * scaleFactor));
    }

    // Method to create a scale from the center of the object being scaled

    public static void glCenterScale(double width, double height, double scale) {
        if (scale > 1.0D)
            scale = 1.0D;
        GL11.glTranslated((1 - scale) * width, (1 - scale) * height, 0);
        GL11.glScaled(scale, scale, 0.0D);
    }

    public static void glColor(final int color) {
        glColor4ub((byte)(color >> 16 & 0xFF), (byte)(color >> 8 & 0xFF), (byte)(color & 0xFF), (byte)(color >> 24 & 0xFF));
    }

    public static class Vec2d {

        public double x, y;

        public Vec2d(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    public static void summerTargetHUD(EntityPlayer player) {
        // Change
        final double x = 0, y = 0;

        Gui.drawRect(x, y, 100, 50, 0xFF000000);
    }
}
