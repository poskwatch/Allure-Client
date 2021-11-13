package vip.allureclient.base.util.visual;

import net.minecraft.entity.EntityLivingBase;
import vip.allureclient.base.util.math.MathUtil;

import java.awt.*;

public class ColorUtil {

    public static int getHealthColor(EntityLivingBase e){
        final double health = e.getHealth();
        final double maxHealth = e.getMaxHealth();
        final double healthPercentage = health / maxHealth;
        final Color c1 = Color.GREEN;
        final Color c2 = Color.RED;
        final Color healthColor = new Color(
                (int) (MathUtil.linearInterpolate(c1.getRed(), c2.getRed(), healthPercentage)),
                (int) (MathUtil.linearInterpolate(c1.getGreen(), c2.getGreen(), healthPercentage)),
                (int) (MathUtil.linearInterpolate(c1.getBlue(), c2.getBlue(), healthPercentage))
        );
        return healthColor.getRGB();
    }

}
