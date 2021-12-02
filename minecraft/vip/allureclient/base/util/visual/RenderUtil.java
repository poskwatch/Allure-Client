package vip.allureclient.base.util.visual;

import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;
import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class RenderUtil {

    public static void drawBorderedRect(double x, double y, double width, double height, int boxColor, int outlineColor) {
        Gui.drawRectWithWidth(x, y, width, height, boxColor);
        Gui.drawRectWithWidth(x - 1, y - 1, 1, height + 2, outlineColor);
        Gui.drawRectWithWidth(x + width, y - 1, 1, height + 2, outlineColor);
        Gui.drawRectWithWidth(x, y - 1, width, 1, outlineColor);
        Gui.drawRectWithWidth(x, y + height, width, 1, outlineColor);
    }

    public static double easeOutAnimation(double target, double current, double speed) {
        boolean larger = (target > current);
        if (speed < 0.0D) {
            speed = 0.0D;
        } else if (speed > 1.0D) {
            speed = 1.0D;
        }
        double dif = Math.max(target, current) - Math.min(target, current);
        double factor = dif * speed;
        if (factor < 0.1D)
            factor = 0.1D;
        if (larger) {
            current += factor;
        } else {
            current -= factor;
        }
        return current;
    }

    public static void glColor(final int color) {
        GL11.glColor4f(new Color(color).getRed() / 255.0F, new Color(color).getGreen() / 255.0F, new Color(color).getBlue() / 255.0F, (int) (new Color(color).getAlpha() / 255.0F));
    }
}
