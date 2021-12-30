package vip.allureclient.visual.notification;

import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.util.glu.GLU;
import vip.allureclient.AllureClient;
import vip.allureclient.base.font.MinecraftFontRenderer;
import vip.allureclient.base.util.client.TimerUtil;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.base.util.visual.AnimatedCoordinate;
import vip.allureclient.base.util.visual.ChatUtil;
import vip.allureclient.base.util.visual.GLUtil;

import java.awt.*;

public class Notification {

    private final String title, body;
    private double width, height;
    private final int color;
    private final long duration;
    private final NotificationType type;
    private final AnimatedCoordinate animatedCoordinate;
    private final MinecraftFontRenderer fontRenderer = AllureClient.getInstance().getFontManager().smallFontRenderer;
    private final TimerUtil timerUtil = new TimerUtil();

    public boolean completed;

    public Notification(String title, String body, long duration, NotificationType type) {
        this.title = title;
        this.body = body;
        this.duration = duration;
        this.type = type;
        this.height = 25;
        this.width = Math.max(fontRenderer.getStringWidth(title), fontRenderer.getStringWidth(body)) + 4;
        this.color = type.getColor();
        this.timerUtil.reset();
        this.completed = false;

        ScaledResolution scaledResolution = new ScaledResolution(Wrapper.getMinecraft());
        this.animatedCoordinate = new AnimatedCoordinate(scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight());
    }

    public void render(ScaledResolution scaledResolution, double yOffset, int index) {
        final int screenWidth = scaledResolution.getScaledWidth();
        final int screenHeight = scaledResolution.getScaledHeight();
        final double xPosition = screenWidth - width;
        final double yPosition = screenHeight - (height + 2) - yOffset - (index * (height + 1));

        if (this.timerUtil.hasReached(duration))
            animatedCoordinate.animateCoordinates(screenWidth, yPosition, AnimatedCoordinate.AnimationType.EASE_OUT);
        else {
            animatedCoordinate.animateCoordinates(xPosition, yPosition, AnimatedCoordinate.AnimationType.EASE_OUT);
        }

        final float x = (float) animatedCoordinate.getX();
        final float y = (float) animatedCoordinate.getY();

        if (x >= screenWidth) {
            completed = true;
            return;
        }

        GLUtil.glFilledQuad(x, y, width, height, 0x90000000);
        final double progress = (double) (System.currentTimeMillis() - this.timerUtil.lastMS) / this.duration * width;
        GLUtil.glFilledQuad(x, y + (height - 2), width, 2, new Color(color).darker().darker().darker().darker().getRGB());
        GLUtil.glFilledQuad(x, y + (height - 2), width - progress, 2, color);
        fontRenderer.drawStringWithShadow(title, x + 3, y + 3, -1);
        fontRenderer.drawStringWithShadow(body, x + 3, y + 13, 0xff909090);
    }

    public NotificationType getType() {
        return type;
    }

    public long getDuration() {
        return duration;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }
}
