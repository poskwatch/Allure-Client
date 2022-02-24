package vip.allureclient.impl.module.player;

import io.github.poskwatch.eventbus.api.annotations.EventHandler;
import io.github.poskwatch.eventbus.api.enums.Priority;
import io.github.poskwatch.eventbus.api.interfaces.IEventCallable;
import io.github.poskwatch.eventbus.api.interfaces.IEventListener;
import net.minecraft.network.play.client.C03PacketPlayer;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.impl.event.events.player.UpdatePositionEvent;
import vip.allureclient.impl.property.EnumProperty;

public class NoFall extends Module {

    private final EnumProperty<NoFallMode> mode = new EnumProperty<>("Mode", NoFallMode.EDIT, this);

    public NoFall() {
        super("No Fall", ModuleCategory.PLAYER);
        this.setListener(new IEventListener() {
            @EventHandler(events = UpdatePositionEvent.class, priority = Priority.LOW)
            final IEventCallable<UpdatePositionEvent> onUpdatePosition = (event -> {
                if(event.isPre()) {
                    if (mode.getPropertyValue().equals(NoFallMode.PACKET)) {
                        if (mc.thePlayer.fallDistance >= 3)
                            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
                    }
                    if (mode.getPropertyValue().equals(NoFallMode.EDIT)) {
                        if (mc.thePlayer.fallDistance >= 3)
                            event.setOnGround(true);
                    }
                }
            });
        });
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    private enum NoFallMode {
        PACKET("Packet"),
        EDIT("Edit");

        private final String name;

        NoFallMode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
