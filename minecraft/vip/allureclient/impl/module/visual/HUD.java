package vip.allureclient.impl.module.visual;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import vip.allureclient.AllureClient;
import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.font.MinecraftFontRenderer;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.ModuleCategory;
import vip.allureclient.base.module.ModuleData;
import vip.allureclient.base.util.client.NetworkUtil;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.base.util.player.MovementUtil;
import vip.allureclient.base.util.visual.ColorUtil;
import vip.allureclient.base.util.visual.RenderUtil;
import vip.allureclient.impl.event.visual.Render2DEvent;
import vip.allureclient.impl.property.BooleanProperty;
import vip.allureclient.impl.property.ColorProperty;
import vip.allureclient.impl.property.EnumProperty;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@ModuleData(moduleName = "HUD", moduleBind = 0, moduleCategory = ModuleCategory.VISUAL)
public class HUD extends Module {

    @EventListener
    EventConsumer<Render2DEvent> onRender2DEvent;

    private final EnumProperty<ListColorModes> listColorProperty = new EnumProperty<>("List Color", ListColorModes.Client, this);

    private final EnumProperty<ListOutlineModes> listOutlineModeProperty = new EnumProperty<>("List Outline", ListOutlineModes.Right, this);

    private final EnumProperty<WatermarkModes> waterMarkModeProperty = new EnumProperty<>("Watermark", WatermarkModes.New, this);

    private final BooleanProperty playerInfoProperty = new BooleanProperty("Player Info", true, this);

    public HUD() {
        this.onRender2DEvent = (render2DEvent -> {
            final int color = ClientColor.getInstance().getColorRGB();

            final int height = render2DEvent.getScaledResolution().getScaledHeight(), width = render2DEvent.getScaledResolution().getScaledWidth();

            final MinecraftFontRenderer fontRenderer = AllureClient.getInstance().getFontManager().mediumFontRenderer;

            String watermark = String.format("%s v%s", AllureClient.getInstance().CLIENT_NAME, AllureClient.getInstance().CLIENT_VERSION);
            if (waterMarkModeProperty.getPropertyValue().equals(WatermarkModes.New))
                watermark = String.format("\247r%s\247f%s v%s", AllureClient.getInstance().CLIENT_NAME.charAt(0), AllureClient.getInstance().CLIENT_NAME.substring(1), AllureClient.getInstance().CLIENT_VERSION);

            if (waterMarkModeProperty.getPropertyValue().equals(WatermarkModes.New) || waterMarkModeProperty.getPropertyValue().equals(WatermarkModes.Solid)) {
                fontRenderer.drawStringWithShadow(watermark, 6, 6, color);
            }
            else {
                final String csgoWatermark = String.format("allureclient.vip \2477|\247f FPS: %s \2477|\247f %s", Minecraft.getDebugFPS(),
                        Wrapper.getMinecraft().getCurrentServerData() == null ? "Singleplayer" : Wrapper.getMinecraft().getCurrentServerData().serverIP);
                RenderUtil.drawBorderedRect(5, 5, fontRenderer.getStringWidth(csgoWatermark) + 5, 15, 0x90000000, 0x20ffffff);
                Gui.drawHorizontalGradient(5, 5, (float) (fontRenderer.getStringWidth(csgoWatermark) + 5), 1,
                        ColorUtil.interpolateColorsDynamic(7, 1, Color.MAGENTA, Color.BLUE).getRGB(),
                        ColorUtil.interpolateColorsDynamic(7, 1, Color.BLUE, Color.MAGENTA).getRGB());
                fontRenderer.drawStringWithShadow(csgoWatermark, 7, 10, -1);
            }

            if (playerInfoProperty.getPropertyValue()) {
                fontRenderer.drawStringWithShadow("Ping: \2477" + NetworkUtil.getPing(), 1, height - 29, -1);
                fontRenderer.drawStringWithShadow("Block/s: \2477" + MovementUtil.getPlayerBPS(), 1, height - 19, -1);
                fontRenderer.drawStringWithShadow(String.format("%.1f, %.1f, %.1f", Wrapper.getPlayer().posX, Wrapper.getPlayer().posY, Wrapper.getPlayer().posZ), 1, height - 9, -1);
            }


            final ArrayList<Module> sortedDisplayModules = new ArrayList<>(AllureClient.getInstance().getModuleManager().getSortedDisplayModules.apply(AllureClient.getInstance().getFontManager().mediumFontRenderer));
            final ScaledResolution sr = render2DEvent.getScaledResolution();
            final AtomicInteger moduleDrawCount = new AtomicInteger();
            sortedDisplayModules.forEach(module -> {
                final double posX = render2DEvent.getScaledResolution().getScaledWidth() - fontRenderer.getStringWidth(module.getModuleDisplayName()) - 3;
                final double posY = 3 + moduleDrawCount.get() * 13;
                int listColor;
                switch (listColorProperty.getPropertyValue()) {
                    case Dynamic:
                        listColor = ColorUtil.interpolateColorsDynamic(5, moduleDrawCount.get() * 25, new Color(color), new Color(color).darker().darker().darker()).getRGB();
                        break;
                    case Client:
                        listColor = ColorUtil.interpolateColorsDynamic(5, moduleDrawCount.get() * 25, new Color(0x6042cef5), new Color(0xffd742f5)).getRGB();
                        break;
                    default:
                        listColor = color;
                        break;
                }
                Gui.drawRectWithWidth((float) posX - 2, (float) posY - 4,
                        (float) fontRenderer.getStringWidth(module.getModuleDisplayName()) + 7, 13, 0x50000000);
                fontRenderer.drawStringWithShadow(module.getModuleDisplayName(), posX, posY, listColor);
                switch (listOutlineModeProperty.getPropertyValue()) {
                    case Left:
                        GlStateManager.color(1, 1, 1);
                        Gui.drawRectWithWidth((float) (posX - 3), (float) (posY - 4), 1, 13, listColor);
                        break;
                    case Right:
                        GlStateManager.color(1, 1, 1);
                        Gui.drawRectWithWidth(render2DEvent.getScaledResolution().getScaledWidth() - 1, (float) (posY - 4), 1, 13, listColor);
                        break;
                    case Outline:
                        GlStateManager.color(1, 1, 1);
                        int toggledIndex = sortedDisplayModules.indexOf(module);
                        int m1Offset = -1;
                        if (toggledIndex != sortedDisplayModules.size() - 1) {
                            m1Offset += fontRenderer.getStringWidth(sortedDisplayModules.get(toggledIndex + 1).getModuleDisplayName());
                            m1Offset += 6;
                        }
                        GlStateManager.color(1, 1, 1, 1);
                        Gui.drawRect(
                                sr.getScaledWidth() - fontRenderer.getStringWidth(module.getModuleDisplayName()) - 5,
                                posY + 1 + fontRenderer.getHeight(),
                                sr.getScaledWidth() - m1Offset,
                                posY + 2 + fontRenderer.getHeight(),
                                listColor);
                        Gui.drawRectWithWidth((int) posX - 2, posY - 4, 1, 14, listColor);
                        break;
                }

                moduleDrawCount.getAndIncrement();
            });
        });
        setModuleToggled(true);
    }

    private enum ListColorModes {
        Solid,
        Dynamic,
        Client
    }

    private enum ListOutlineModes {
        Left,
        Right,
        Outline
    }

    private enum WatermarkModes {
        Solid,
        New,
        CSGO
    }
}
