package vip.allureclient.visual.console;

import vip.allureclient.AllureClient;
import vip.allureclient.base.util.client.TimerUtil;
import vip.allureclient.base.util.visual.AnimatedCoordinate;

import java.awt.*;

public class ConsoleLine {

    private final String lineMessage;
    private final Color color;
    private boolean dead;
    private final TimerUtil timerUtil;
    private double alphaAnimation;

    public ConsoleLine(String lineMessage, Color color) {
        this.lineMessage = lineMessage;
        this.color = color;
        this.timerUtil = new TimerUtil();
    }

    public void render(int index) {
        float y = 6;
        y += index * 12;
        double alphaTarget;
        AllureClient.getInstance().getFontManager().csgoFontRenderer.drawStringWithShadow(lineMessage,
                160, y, color.getRGB());
        if (y >= 50 || timerUtil.hasReached(1000)) {

        }
    }

    public boolean isDead() {
        return dead;
    }
}
