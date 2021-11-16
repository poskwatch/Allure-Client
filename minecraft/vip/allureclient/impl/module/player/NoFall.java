package vip.allureclient.impl.module.player;

import net.minecraft.network.play.client.C03PacketPlayer;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.ModuleCategory;
import vip.allureclient.base.module.ModuleData;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.impl.event.player.UpdatePositionEvent;
import vip.allureclient.impl.property.EnumProperty;

@ModuleData(moduleName = "NoFall", keyBind = 0, category = ModuleCategory.PLAYER)
public class NoFall extends Module {

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;

    private final EnumProperty<NoFallModes> mode = new EnumProperty<>("Mode", NoFallModes.WATCHDOG, this);

    private enum NoFallModes {
        VANILLA,
        WATCHDOG
    }

    public NoFall() {
        this.onUpdatePositionEvent = (updatePositionEvent -> {
            if(updatePositionEvent.isPre()) {
                if (mode.getPropertyValue().equals(NoFallModes.VANILLA)) {
                    if (Wrapper.getPlayer().fallDistance > 2) {
                        Wrapper.getPlayer().sendQueue.addToSendQueue(new C03PacketPlayer(true));
                    }
                }
                if (mode.getPropertyValue().equals(NoFallModes.WATCHDOG)) {
                    if (Wrapper.getPlayer().fallDistance > 2 && Wrapper.getPlayer().fallDistance < 10) {
                        updatePositionEvent.setOnGround(true);
                    }
                }
            }
        });
    }

}
