package vip.allureclient.visual.screens.dropdown.component.sub.impl;

import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import vip.allureclient.AllureClient;
import vip.allureclient.base.util.visual.ColorUtil;
import vip.allureclient.base.util.visual.GLUtil;
import vip.allureclient.base.util.visual.RenderUtil;
import vip.allureclient.impl.property.ColorProperty;
import vip.allureclient.visual.screens.dropdown.component.Component;
import vip.allureclient.visual.screens.dropdown.component.sub.ModuleComponent;

import java.awt.*;

public class ColorPropertyComponent extends Component {

    private final ModuleComponent parent;
    ColorProperty colorProperty;
    private int offset;
    private boolean expanded;

    public ColorPropertyComponent(ColorProperty colorProperty, ModuleComponent parent, int offset){
        this.parent = parent;
        this.colorProperty = colorProperty;
        this.offset = offset;
    }

    @Override
    public void onDrawScreen(int mouseX, int mouseY) {
        double x = parent.getParentFrame().getX();
        double y = parent.getParentFrame().getY() + offset;

        Gui.drawRectWithWidth(x, y, 115, 14, isMouseHovering(mouseX, mouseY) ? 0xff101010 : 0xff151515);
        AllureClient.getInstance().getFontManager().smallFontRenderer.drawStringWithShadow(colorProperty.getPropertyLabel(), x + 3, y + 4, -1);

        Gui.drawRectWithWidth(x + 115 - 24, y + 2, 22, 10, 0x99000000);

        drawCheckeredBackground(x + 115 - 23, y + 3, 2, 10, 4);
        Gui.drawRectWithWidth(x + 115 - 23, y + 3, 20, 8, colorProperty.getPropertyValueRGB());

        if (expanded) {
            double pickerY = y + 17;

            final float[] hsb = Color.RGBtoHSB(colorProperty.getPropertyValue().getRed(), colorProperty.getPropertyValue().getGreen(), colorProperty.getPropertyValue().getBlue(), null);

            // Draw main color picker
            Gui.drawRectWithWidth(x + 2, pickerY - 1, 80, 80, 0xff000000);
            drawColourPicker(x + 3, pickerY, 78, 78, hsb[0]);
            // Draw indicator
            RenderUtil.drawBorderedRect(x + 3 + hsb[1] * 78 - 1, pickerY + (1.0f - hsb[2]) * 78 - 1, 1, 1, 0, 0xff000000);

            // Draw hue slider
            Gui.drawRectWithWidth(x + 83, pickerY - 1, 12, 80, 0xff000000);
            drawHueSlider(x + 2 + 82, pickerY, 10, 78);
            // Draw indicator
            RenderUtil.drawBorderedRect(x + 83.5, pickerY + hsb[0] * 78 - 1, 11.5, 1, -1, 0xff000000);

            // Draw alpha slider
            Gui.drawRectWithWidth(x + 97, pickerY - 1, 14, 80, 0xff000000);
            drawCheckeredBackground(x + 98, pickerY, 3, 4, 26);
            final Color color = colorProperty.getPropertyValue();
            Gui.drawGradientRect(x + 98, pickerY, x + 98 + 12, pickerY + 78, 0,
                    new Color(color.getRed(), color.getGreen(), color.getBlue()).getRGB());
            // Draw indicator
            RenderUtil.drawBorderedRect(x + 97.5, pickerY + ((colorProperty.getPropertyValueRGB() >> 24 & 0xFF) / 255.0f) * 78 - 1, 13.5, 1, -1, 0xff000000);

            if (mouseX >= x + 2 && mouseX <= x + 2 + 80 && mouseY >= pickerY && mouseY <= pickerY + 80 && Mouse.isButtonDown(0)) {
                final float saturation = (float) ((mouseX - (x + 2)) / 80);
                final float brightness = 1.0F - (float) ((mouseY - pickerY) / 80);

                final Color newColor = new Color(Color.HSBtoRGB(hsb[0], Math.max(0, Math.min(saturation, 1)), Math.max(0, Math.min(brightness, 1))));
                final Color settingColor = new Color(newColor.getRed(), newColor.getGreen(), newColor.getBlue(), colorProperty.getPropertyValue().getAlpha());

                colorProperty.setPropertyValue(settingColor);
            }

            if (mouseX >= x + 2 + 82 && mouseX <= x + 2 + 82 + 10 && mouseY >= pickerY && mouseY <= pickerY + 80 && Mouse.isButtonDown(0)) {

                final Color newColor = new Color(Color.HSBtoRGB((float) ((mouseY - pickerY) / 80), hsb[1], hsb[2]));
                final Color settingColor = new Color(newColor.getRed(), newColor.getGreen(), newColor.getBlue(), colorProperty.getPropertyValue().getAlpha());

                colorProperty.setPropertyValue(settingColor);
            }

            if (mouseX >= x + 98 && mouseX <= x + 98 + 12 && mouseY >= pickerY && mouseY <= pickerY + 78 && Mouse.isButtonDown(0)) {
                final int alpha = (int) (255 * ((mouseY - pickerY) / 78));
                final Color c = colorProperty.getPropertyValue();
                colorProperty.setPropertyValue(new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha));
            }
        }

        super.onDrawScreen(mouseX, mouseY);
    }

    @SuppressWarnings("SameParameterValue")
    private static void drawColourPicker(final double x,
                                         final double y,
                                         final double width,
                                         final double height,
                                         final float hue) {
        GLUtil.glHorizontalGradientQuad(x, y, width, height, 0xFFFFFFFF, Color.HSBtoRGB(hue, 1.0F, 1.0F));
        Gui.drawGradientRect(x, y, x + width, y + height, 0, 0xFF000000);
    }

    @SuppressWarnings("SameParameterValue")
    private static void drawHueSlider(final double x,
                                      final double y,
                                      final double width,
                                      final double height) {
        GL11.glTranslated(x, y, 0);
        final int[] colours = {
                0xFFFF0000, // red (255, 0, 0)
                0xFFFFFF00, // yellow (255, 255, 0)
                0xFF00FF00, // green (0, 255, 0)
                0xFF00FFFF, // aqua (0, 255, 255)
                0xFF0000FF, // blue (0, 0, 255)
                0xFFFF00FF, // purple (255, 0, 255)
                0xFFFF0000, // red (255, 0, 0)
        };
        final double segment = height / colours.length;
        for (int i = 0; i < colours.length; i++) {
            final int colour = colours[i];
            final int top = i != 0 ? ColorUtil.interpolateColors(new Color(colours[i - 1]), new Color(colour), 0.5f).getRGB() : colour;
            final int bottom = i + 1 < colours.length ? ColorUtil.interpolateColors(new Color(colour), new Color(colours[i + 1]), 0.5f).getRGB() : colour;
            final double start = segment * i;
            Gui.drawGradientRect(0, start, width, start + segment, top, bottom);
        }
        GL11.glTranslated(-x, -y, 0);
    }

    @SuppressWarnings("all")
    private void drawCheckeredBackground(final double x, final double y, final double sqWidth, final int sqsWide, final int sqsHigh) {
        Gui.drawRectWithWidth(x, y, sqWidth * sqsWide, sqWidth * sqsHigh, 0xFFFFFFFF);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glTranslated(x, y, 0);
        GLUtil.glColor(0xFFBFBFBF);
        GL11.glBegin(GL11.GL_QUADS);
        {
            double sqX = 0;
            double sqY = 0;
            for (int j = 0; j < sqsHigh; j++) {
                for (int i = 0; i < sqsWide / 2; i++) {
                    GL11.glVertex2d(sqX, sqY);
                    GL11.glVertex2d(sqX, sqY + sqWidth);
                    GL11.glVertex2d(sqX + sqWidth, sqY + sqWidth);
                    GL11.glVertex2d(sqX + sqWidth, sqY);

                    sqX += sqWidth * 2.0;
                }

                sqX = j % 2 == 0 ? sqWidth : 0;
                sqY += sqWidth;
            }
        }
        GL11.glEnd();
        GL11.glTranslated(-x, -y, 0);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    @Override
    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isMouseHovering(mouseX, mouseY) && mouseButton == 1) {
            expanded = !expanded;
        }
    }

    @Override
    public double getHeight() {
        return expanded ? 97 : 14;
    }

    private boolean isMouseHovering(int mouseX, int mouseY){
        double x = parent.getParentFrame().getX();
        double y = parent.getParentFrame().getY() + offset;
        return mouseX >= x && mouseX <= x + 115 && mouseY >= y && mouseY <= y + 14;
    }

    @Override
    public boolean isHidden() {
        return colorProperty.isPropertyHidden();
    }

    @Override
    public void setOffset(int offset) {
        this.offset = offset;
    }
}
