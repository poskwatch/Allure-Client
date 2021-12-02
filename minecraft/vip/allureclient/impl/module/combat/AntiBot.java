package vip.allureclient.impl.module.combat;

import vip.allureclient.AllureClient;
import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.ModuleCategory;
import vip.allureclient.base.module.ModuleData;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.impl.event.player.UpdatePositionEvent;
import vip.allureclient.impl.property.EnumProperty;

@ModuleData(moduleName = "Anti Bot", moduleBind = 0, moduleCategory = ModuleCategory.COMBAT)
public class AntiBot extends Module {

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;

    public final EnumProperty<AntiBotMode> antiBotModeProperty = new EnumProperty<>("Mode", AntiBotMode.Watchdog, this);

    public AntiBot() {
        onUpdatePositionEvent = (updatePositionEvent -> Wrapper.getWorld().playerEntities.forEach(entity -> {
            if (antiBotModeProperty.getPropertyValue().equals(AntiBotMode.Simple)) {
                if (entity.isInvisible() && entity != Wrapper.getPlayer())
                    Wrapper.getWorld().removeEntity(entity);
            }
        }));
        antiBotModeProperty.onValueChange = () -> setModuleSuffix(antiBotModeProperty.getEnumValueAsString());
    }

    public static AntiBot getInstance() {
        return (AntiBot) AllureClient.getInstance().getModuleManager().getModuleByClass.apply(AntiBot.class);
    }

    public enum AntiBotMode {
        Watchdog,
        Simple,
        Advanced
    }

}

