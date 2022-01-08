package vip.allureclient.impl.module.visual;

import vip.allureclient.AllureClient;
import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.annotations.ModuleData;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.util.client.NetworkUtil;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.base.util.visual.GLUtil;
import vip.allureclient.impl.event.network.ServerConnectEvent;
import vip.allureclient.impl.event.visual.Render2DEvent;

import java.awt.*;

@ModuleData(moduleName = "Statistics", moduleCategory = ModuleCategory.VISUAL, moduleBind = 0)
public class Statistics extends Module {

    @EventListener
    EventConsumer<Render2DEvent> onRender2DEvent;

    @EventListener
    EventConsumer<ServerConnectEvent> onServerConnectEvent;

    private int kills, wins;

    private final long sessionTime;
    private long hypixelTime;

    public Statistics() {
        this.sessionTime = System.currentTimeMillis();
        this.onRender2DEvent = (event -> {
            GLUtil.glOutlinedFilledQuad(5, 23, 125, 68, 0x90202020, 0x90303030);
            GLUtil.glOutlinedFilledQuad(10, 28, 115, 58, 0x70000000, 0x90303030);
            AllureClient.getInstance().getFontManager().csgoFontRenderer.drawCenteredStringWithShadow(String.format(
                    "Statistics \2477(%s)", Wrapper.getMinecraft().getSession().getUsername()
            ), 65.5f, 32, -1);
            GLUtil.glFilledQuad(13, 39, 109, 1, Color.MAGENTA.getRGB());
            GLUtil.glFilledQuad(13, 40, 109, 1, Color.MAGENTA.darker().darker().getRGB());
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
        this.onServerConnectEvent = (event -> {
            if (event.getIP() != null && event.getIP().contains("hypixel"))
                resetHypixelTime();
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
        return (Statistics) AllureClient.getInstance().getModuleManager().getModuleByClass.apply(Statistics.class);
    }

}
