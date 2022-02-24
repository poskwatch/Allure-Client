package vip.allureclient.base.util.visual;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import java.io.IOException;
import java.io.InputStream;

public class SoundUtil {

    public static void playSound(String resourceLocation) {
        try {
            InputStream sound = getFileFromResourceAsStream("assets/minecraft/Allure/Sounds/" + resourceLocation);
            AudioStream stream = new AudioStream(sound);
            AudioPlayer.player.start(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static InputStream getFileFromResourceAsStream(String fileName) {
        ClassLoader classLoader = SoundUtil.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            return inputStream;
        }
    }
}
