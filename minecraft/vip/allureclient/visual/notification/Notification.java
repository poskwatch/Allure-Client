package vip.allureclient.visual.notification;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import vip.allureclient.AllureClient;
import vip.allureclient.base.font.MinecraftFontRenderer;

import java.awt.*;

public class Notification {

    private final String title;
    private final String body;
    private final MinecraftFontRenderer fr = AllureClient.getInstance().getFontManager().smallFontRenderer;

    private final NotificationType type;

    private long start;

    private final long fadedIn;
    private final long fadeOut;
    private final long end;

    public Notification(String title, String body, long showTime, NotificationType type){
        this.title = title;
        this.body = body;
        this.type = type;

        fadedIn = 200 * showTime;
        fadeOut = fadedIn + 500 * showTime;
        end = fadeOut + fadedIn;
    }

    public void show(){
        start = System.currentTimeMillis();
    }

    public boolean isShown() {
        return getTime() <= end;
    }

    private long getTime() {
        return System.currentTimeMillis() - start;
    }

    public void render(){
        double offset;

        double width = fr.getStringWidth(body.replaceAll("\247", ""));
        double height = 27;
        long time = getTime();

        if (time < fadedIn) {
            offset = Math.tanh(time / (double) (fadedIn) * Math.PI) * width;
        } else if (time > fadeOut) {
            offset = (Math.tanh(Math.PI - (time - fadeOut) / (double) (end - fadeOut) * Math.PI) * width);
        } else {
            offset = width;
        }

        Color color = new Color(0, 0, 0, 180);
        Color notificationColor = type.notificationColor;

        Gui.drawRect(GuiScreen.width - offset - 3, GuiScreen.height - 5 - height, GuiScreen.width, GuiScreen.height - 5, color.getRGB());

        GlStateManager.color(1, 1, 1);
        Gui.drawRect(GuiScreen.width, GuiScreen.height - 5, GuiScreen.width - offset - 28 + (time / 11.0F), GuiScreen.height - 7, notificationColor.getRGB());

        fr.drawStringWithShadow(title, (int) (GuiScreen.width - offset), GuiScreen.height - 1 - height, -1);
        fr.drawStringWithShadow("\2477" + body, (int) (GuiScreen.width - offset + 7), GuiScreen.height - 17, -1);
    }


}
