package vip.allureclient.impl.module.combat;

import io.github.poskwatch.eventbus.api.annotations.EventHandler;
import io.github.poskwatch.eventbus.api.enums.Priority;
import io.github.poskwatch.eventbus.api.interfaces.IEventCallable;
import io.github.poskwatch.eventbus.api.interfaces.IEventListener;
import vip.allureclient.AllureClient;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.impl.event.events.player.UpdatePositionEvent;
import vip.allureclient.impl.property.EnumProperty;

public class AntiBot extends Module {

    public final EnumProperty<AntiBotMode> antiBotModeProperty = new EnumProperty<>("Mode", AntiBotMode.WATCHDOG, this);

    public AntiBot() {
        super("Anti Bot", ModuleCategory.COMBAT);

        this.setListener(new IEventListener() {
            @EventHandler(events = UpdatePositionEvent.class, priority = Priority.VERY_HIGH)
            final IEventCallable<UpdatePositionEvent> onUpdatePosition = (event ->
                    mc.theWorld.playerEntities.forEach(entity -> {
                if (antiBotModeProperty.getPropertyValue().equals(AntiBotMode.SIMPLE)) {
                    if (entity.isInvisible() && entity != mc.thePlayer)
                        mc.theWorld.removeEntity(entity);
                }
            }));
        });
        antiBotModeProperty.onValueChange = () -> setModuleSuffix(antiBotModeProperty.getEnumValueAsString());
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public enum AntiBotMode {
        WATCHDOG("Watchdog"),
        SIMPLE("Simple"),
        ADVANCED("Advanced");

        private final String name;

        AntiBotMode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public static AntiBot getInstance() {
        return (AntiBot) AllureClient.getInstance().getModuleManager().getModuleOrNull("Anti Bot");
    }
}

