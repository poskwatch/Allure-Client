package vip.allureclient.base.util.visual;

import net.minecraft.client.Minecraft;
import vip.allureclient.base.util.math.MathUtil;

public class AnimationUtil {

    public static double easeOutAnimation(double target, double current, double speed) {
        boolean larger = (target > current);
        double dif = Math.max(target, current) - Math.min(target, current);
        double factor = dif * speed;
        if (larger) {
            current += factor;
        } else {
            current -= factor;
        }
        return current;
    }

    public static double linearAnimation(double target, double current, double speed) {
        double dif = Math.abs(current - target);
        int fps = Minecraft.getDebugFPS();
        if (dif > 0.0D) {
            double animationSpeed = MathUtil.roundToPlace(Math.min(
                    10.0D, Math.max(0.005D, 144.0D / fps * speed)), 3);
            if (dif < animationSpeed) {
                animationSpeed = dif;
            }
            if (current < target)
                return current + animationSpeed;
            if (current > target) {
                return current - animationSpeed;
            }
        }
        return current;
    }

}
