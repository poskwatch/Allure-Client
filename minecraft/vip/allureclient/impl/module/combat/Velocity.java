package vip.allureclient.impl.module.combat;

import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.ModuleCategory;
import vip.allureclient.base.module.ModuleData;
import vip.allureclient.impl.event.network.PacketReceiveEvent;

@ModuleData(moduleName = "Velocity", moduleBind = 0, moduleCategory = ModuleCategory.COMBAT)
public class Velocity extends Module {

    @EventListener
    EventConsumer<PacketReceiveEvent> onPacketReceiveEvent;

    public Velocity() {
        onPacketReceiveEvent = (packetReceiveEvent -> {
           if(packetReceiveEvent.getPacket() instanceof S27PacketExplosion || packetReceiveEvent.getPacket() instanceof S12PacketEntityVelocity) {
               packetReceiveEvent.setCancelled(true);
           }
        });
    }
}
