package vip.allureclient.base.util.visual;

import net.minecraft.client.gui.Gui;

public class RenderUtil {

    public static void drawBorderedRect(double x, double y, double width, double height, int boxColor, int outlineColor) {
        Gui.drawRectWithWidth(x, y, width, height, boxColor);
        Gui.drawRectWithWidth(x - 1, y - 1, 1, height + 2, outlineColor);
        Gui.drawRectWithWidth(x + width, y - 1, 1, height + 2, outlineColor);
        Gui.drawRectWithWidth(x, y - 1, width, 1, outlineColor);
        Gui.drawRectWithWidth(x, y + height, width, 1, outlineColor);
    }
}
