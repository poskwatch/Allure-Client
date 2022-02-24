package vip.allureclient.impl.module.combat;

import io.github.poskwatch.eventbus.api.annotations.EventHandler;
import io.github.poskwatch.eventbus.api.enums.Priority;
import io.github.poskwatch.eventbus.api.interfaces.IEventCallable;
import io.github.poskwatch.eventbus.api.interfaces.IEventListener;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.impl.event.events.network.PacketReceiveEvent;

public class Velocity extends Module {

    public Velocity() {
        super("Velocity", ModuleCategory.COMBAT);
        this.setListener(new IEventListener() {
            @EventHandler(events = PacketReceiveEvent.class, priority = Priority.HIGH)
            final IEventCallable<PacketReceiveEvent> onReceivePacket = (event -> {
                final Packet<?> packet = event.getPacket();
                if (packet instanceof S12PacketEntityVelocity) {
                    S12PacketEntityVelocity s12 = (S12PacketEntityVelocity) packet;
                    if (s12.getEntityID() == mc.thePlayer.getEntityId())
                        event.setCancelled();
                }
                if (packet instanceof S27PacketExplosion)
                    event.setCancelled();
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
}
