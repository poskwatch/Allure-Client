package vip.allureclient.base.util.visual;

import net.minecraft.entity.EntityLivingBase;
import vip.allureclient.base.util.math.MathUtil;

import java.awt.*;

public class ColorUtil {

    public static Color getHealthColor(EntityLivingBase e, int alpha){
        final double health = e.getHealth();
        final double maxHealth = e.getMaxHealth();
        final double healthPercentage = health / maxHealth;
        final Color c1 = Color.RED;
        final Color c2 = Color.GREEN;
        final Color healthColor = new Color(
                (int) (MathUtil.linearInterpolate(c1.getRed(), c2.getRed(), healthPercentage)),
                (int) (MathUtil.linearInterpolate(c1.getGreen(), c2.getGreen(), healthPercentage)),
                (int) (MathUtil.linearInterpolate(c1.getBlue(), c2.getBlue(), healthPercentage)), alpha
        );
        return healthColor.darker();
    }

    public static Color interpolateColors(Color color1, Color color2, float point) {
        if (point > 1)
            point = 1;
        return new Color((int) ((color2.getRed() - color1.getRed()) * point + color1.getRed()),
                (int) ((color2.getGreen() - color1.getGreen()) * point + color1.getGreen()),
                (int) ((color2.getBlue() - color1.getBlue()) * point + color1.getBlue()));
    }

    public static Color interpolateColorsDynamic(double speed, int index, Color start, Color end) {
        int angle = (int) (((System.currentTimeMillis()) / speed + index) % 360);
        angle = (angle >= 180 ? 360 - angle : angle) * 2;
        return interpolateColors(start, end, angle / 360f);
    }

    public static int getRainbowColor(float speed, int index, float saturation) {
        float hue = ((System.currentTimeMillis() + index) % (int)(speed*1000)) / (speed * 1000);
        return Color.HSBtoRGB(hue, saturation, 1);
    }

    public static Color changeAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    public static Color[] getClientColors() {
        return clientColors;
    }

    private static final Color[] clientColors = {  new Color(0xffff70bf), new Color(0xff9234eb)    };

    public static Color getClientColor(ClientColor color) {
        return color == ClientColor.SECONDARY ? clientColors[0] : clientColors[1];
    }

    public enum ClientColor {
        PRIMARY,
        SECONDARY
    }
}
