package vip.allureclient.base.util.math;

public class MathUtil {

    public static double linearInterpolate(double min, double max, double norm){
        return (max - min) * norm + min;
    }

    public static double animateDoubleValue(double target, double current, double speed){
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
}
