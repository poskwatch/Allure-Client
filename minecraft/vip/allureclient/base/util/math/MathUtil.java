package vip.allureclient.base.util.math;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathUtil {

    public static double linearInterpolate(double min, double max, double norm){
        return (max - min) * norm + min;
    }

    public static double roundToPlace(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static double getRandomNumber(double min, double max) {
        return ((Math.random() * (min/max)) + min);
    }
}
