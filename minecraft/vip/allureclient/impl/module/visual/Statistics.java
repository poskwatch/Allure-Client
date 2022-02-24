package vip.allureclient.impl.module.visual;

import io.github.poskwatch.eventbus.api.annotations.EventHandler;
import io.github.poskwatch.eventbus.api.interfaces.IEventCallable;
import io.github.poskwatch.eventbus.api.interfaces.IEventListener;
import vip.allureclient.AllureClient;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.util.client.NetworkUtil;
import vip.allureclient.base.util.visual.glsl.GLUtil;
import vip.allureclient.impl.event.events.network.ServerConnectEvent;
import vip.allureclient.impl.event.events.visual.Render2DEvent;

import java.awt.*;

public class Statistics extends Module {

    private int kills, wins;

    private final long sessionTime;
    private long hypixelTime;

    public Statistics() {
        super("Statistics", ModuleCategory.VISUAL);
        this.sessionTime = System.currentTimeMillis();
        this.setListener(new IEventListener() {
            @EventHandler(events = Render2DEvent.class)
            final IEventCallable<Render2DEvent> onRender2D = (event -> {
                GLUtil.glOutlinedFilledQuad(5, 23, 125, 68, 0x90202020, 0x90303030);
                GLUtil.glOutlinedFilledQuad(10, 28, 115, 58, 0x70000000, 0x90303030);
                AllureClient.getInstance().getFontManager().csgoFontRenderer.drawCenteredStringWithShadow(String.format(
                        "Statistics \2477(%s)", mc.getSession().getUsername()
                ), 65.5f, 32, -1);
                GLUtil.drawFilledRectangle(13, 39, 109, 1, Color.MAGENTA.getRGB());
                GLUtil.drawFilledRectangle(13, 40, 109, 1, Color.MAGENTA.darker().darker().getRGB());
                AllureClient.getInstance().getFontManager().csgoFontRenderer.drawStringWithShadow("Kills: " + kills, 13, 46, -1);
                AllureClient.getInstance().getFontManager().csgoFontRenderer.drawStringWithShadow("Wins: " + wins, 13, 56, -1);
                int diff = (int) ((int) System.currentTimeMillis() - sessionTime);
                AllureClient.getInstance().getFontManager().csgoFontRenderer.drawStringWithShadow("Session Time: " +
                        diff / (60 * 60 * 1000) % 24 + "h " + diff / (60 * 1000) % 60 + "m " + diff / 1000 % 60 + "s", 13, 66, -1);
                int hypixelDiff = (int) ((int) System.currentTimeMillis() - hypixelTime);
                AllureClient.getInstance().getFontManager().csgoFontRenderer.drawStringWithShadow("Hypixel Time: " +
                        (NetworkUtil.isOnHypixel() ?
                                hypixelDiff / (60 * 60 * 1000) % 24 + "h " + hypixelDiff / (60 * 1000) % 60 + "m " + hypixelDiff / 1000 % 60 + "s" : "\2477n/a"), 13, 76, -1);
            });
            @EventHandler(events = ServerConnectEvent.class)
            final IEventCallable<ServerConnectEvent> onServerConnect = (event -> {
                if (event.getIP() != null && event.getIP().contains("hypixel"))
                    resetHypixelTime();
            });
        });
    }

    public void addWin() {
        wins++;
    }

    public void addKill() {
        kills++;
    }

    private void resetHypixelTime() {
        this.hypixelTime = System.currentTimeMillis();
    }

    public static Statistics getInstance() {
        return (Statistics) AllureClient.getInstance().getModuleManager().getModuleOrNull("Statistics");
    }

}
