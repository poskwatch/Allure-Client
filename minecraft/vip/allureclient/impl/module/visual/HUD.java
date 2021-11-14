package vip.allureclient.impl.module.visual;

import vip.allureclient.AllureClient;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.event.Listener;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.ModuleCategory;
import vip.allureclient.base.module.ModuleData;
import vip.allureclient.base.util.visual.RenderUtil;
import vip.allureclient.impl.event.visual.Render2DEvent;

import java.util.concurrent.atomic.AtomicInteger;

@ModuleData(moduleName = "HUD", keyBind = 0, category = ModuleCategory.VISUAL)
public class HUD extends Module {

    @EventListener
    Listener<Render2DEvent> onRender2DEvent;

    public HUD() {

        this.onRender2DEvent = (render2DEvent -> {
            final String watermark = String.format("%s v%s", AllureClient.getInstance().CLIENT_NAME, AllureClient.getInstance().CLIENT_VERSION);
            AllureClient.getInstance().getFontManager().largeFontRenderer.drawStringWithShadow(watermark, 6, 6, -1);

            final AtomicInteger moduleDrawCount = new AtomicInteger();
            AllureClient.getInstance().getModuleManager().getSortedDisplayModules.apply(AllureClient.getInstance().getFontManager().mediumFontRenderer).forEach(module -> {
                final double posX = render2DEvent.getScaledResolution().getScaledWidth() - AllureClient.getInstance().getFontManager().mediumFontRenderer.getStringWidth(module.getModuleDisplayName()) - 3;
                final double posY = 3 + moduleDrawCount.get() * 13;

                RenderUtil.glFilledQuad((float) posX - 2, (float) posY - 4,
                        (float) AllureClient.getInstance().getFontManager().mediumFontRenderer.getStringWidth(module.getModuleDisplayName()) + 7, 13, 0x50000000);
                AllureClient.getInstance().getFontManager().mediumFontRenderer.drawStringWithShadow(module.getModuleDisplayName(), posX, posY, -1);
                moduleDrawCount.getAndIncrement();
            });
        });

        setModuleToggled(true);
    }
}
