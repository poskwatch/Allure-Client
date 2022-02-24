package vip.allureclient.base.util.client;

import java.util.Random;

public class StringUtil {

    public static String randomInArray(String[] array) {
        return array[new Random().nextInt(array.length)];
    }

}
