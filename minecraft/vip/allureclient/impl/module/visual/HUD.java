package vip.allureclient.impl.module.visual;

import net.minecraft.client.gui.Gui;
import vip.allureclient.AllureClient;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.event.Listener;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.ModuleCategory;
import vip.allureclient.base.module.ModuleData;
import vip.allureclient.impl.event.visual.Render2DEvent;

@ModuleData(label = "HUD", keyBind = 0, category = ModuleCategory.VISUAL)
public class HUD extends Module {

    @EventListener
    Listener<Render2DEvent> onRender2DEvent;

    public HUD() {

        this.onRender2DEvent = (render2DEvent -> {
            final String watermark = String.format("%s v%s", AllureClient.getInstance().CLIENT_NAME, AllureClient.getInstance().CLIENT_VERSION);
            AllureClient.getInstance().getFontManager().largeFontRenderer.drawStringWithShadow(watermark, 6, 6, -1);
        });

        setModuleToggled(true);
    }
}
