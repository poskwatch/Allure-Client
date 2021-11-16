package vip.allureclient.impl.module.visual;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import vip.allureclient.AllureClient;
import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.font.MinecraftFontRenderer;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.ModuleCategory;
import vip.allureclient.base.module.ModuleData;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.base.util.visual.ColorUtil;
import vip.allureclient.base.util.visual.RenderUtil;
import vip.allureclient.impl.event.visual.Render2DEvent;
import vip.allureclient.impl.property.EnumProperty;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@ModuleData(moduleName = "HUD", keyBind = 0, category = ModuleCategory.VISUAL)
public class HUD extends Module {

    @EventListener
    EventConsumer<Render2DEvent> onRender2DEvent;

    private final EnumProperty<ListColorModes> listColorProperty = new EnumProperty<>("List Color", ListColorModes.Client, this);

    private final EnumProperty<ListOutlineModes> listOutlineModeProperty = new EnumProperty<>("List Outline", ListOutlineModes.Right, this);

    public HUD() {

        this.onRender2DEvent = (render2DEvent -> {

            setModuleSuffix("Watermark");

            final int color = ClientColor.getInstance().getColorRGB();

            final String watermark = String.format("%s v%s", AllureClient.getInstance().CLIENT_NAME, AllureClient.getInstance().CLIENT_VERSION);
            AllureClient.getInstance().getFontManager().mediumFontRenderer.drawStringWithShadow(watermark, 6, 6, color);

            AllureClient.getInstance().getFontManager().mediumFontRenderer.drawStringWithShadow("FPS: " + Minecraft.getDebugFPS(), 4,
                    render2DEvent.getScaledResolution().getScaledHeight() - (Wrapper.getMinecraft().currentScreen instanceof GuiChat ? 24 : 12), -1);

            final ArrayList<Module> sortedDisplayModules = new ArrayList<>(AllureClient.getInstance().getModuleManager().getSortedDisplayModules.apply(AllureClient.getInstance().getFontManager().mediumFontRenderer));
            final MinecraftFontRenderer fontRenderer = AllureClient.getInstance().getFontManager().mediumFontRenderer;
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
                RenderUtil.glFilledQuad((float) posX - 2, (float) posY - 4,
                        (float) fontRenderer.getStringWidth(module.getModuleDisplayName()) + 7, 13, 0x50000000);
                fontRenderer.drawStringWithShadow(module.getModuleDisplayName(), posX, posY, listColor);
                switch (listOutlineModeProperty.getPropertyValue()) {
                    case Left:
                        GlStateManager.color(1, 1, 1);
                        RenderUtil.glFilledQuad((float) (posX - 3), (float) (posY - 4), 1, 13, listColor);
                        break;
                    case Right:
                        GlStateManager.color(1, 1, 1);
                        RenderUtil.glFilledQuad(render2DEvent.getScaledResolution().getScaledWidth() - 1, (float) (posY - 4), 1, 13, listColor);
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
}
