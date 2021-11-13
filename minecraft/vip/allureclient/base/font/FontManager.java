package vip.allureclient.base.font;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class FontManager {

    private int completed;

    public MinecraftFontRenderer smallFontRenderer;
    private Font smallFontRendererFont;
    public MinecraftFontRenderer mediumFontRenderer;
    private Font mediumFontRendererFont;
    public MinecraftFontRenderer largeFontRenderer;
    private Font largeFontRendererFont;

    public FontManager() {
        setupFonts();
    }

    private Font getFont(Map<String, Font> locationMap, String location, int size) {
        Font font;
        try {
            if (locationMap.containsKey(location)) {
                font = locationMap.get(location).deriveFont(Font.PLAIN, size);
            } else {
                InputStream is = Minecraft.getMinecraft().getResourceManager()
                        .getResource(new ResourceLocation("Allure/Fonts/" + location)).getInputStream();
                font = Font.createFont(0, is);
                locationMap.put(location, font);
                font = font.deriveFont(Font.PLAIN, size);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", Font.PLAIN, 10);
        }
        return font;
    }

    private boolean hasLoaded() {
        return completed >= 3;
    }

    private void setupFonts(){
        new Thread(() ->
        {
            Map<String, Font> locationMap = new HashMap<>();

            smallFontRendererFont = getFont(locationMap, "font.ttf", 17);
            mediumFontRendererFont = getFont(locationMap, "font.ttf", 19);
            largeFontRendererFont = getFont(locationMap, "font.ttf", 21);

            completed++;
        }).start();
        new Thread(() ->
        {
            Map<String, Font> locationMap = new HashMap<>();
            completed++;
        }).start();
        new Thread(() ->
        {
            Map<String, Font> locationMap = new HashMap<>();
            completed++;
        }).start();
        while (!hasLoaded()) {
            try {
                //noinspection BusyWait
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        smallFontRenderer = new MinecraftFontRenderer(smallFontRendererFont, true, true);
        mediumFontRenderer = new MinecraftFontRenderer(mediumFontRendererFont, true, true);
        largeFontRenderer = new MinecraftFontRenderer(largeFontRendererFont, true, true);
    }
}