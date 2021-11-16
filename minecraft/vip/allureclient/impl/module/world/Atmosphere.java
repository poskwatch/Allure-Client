package vip.allureclient.impl.module.world;

import net.minecraft.network.play.server.S03PacketTimeUpdate;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.ModuleCategory;
import vip.allureclient.base.module.ModuleData;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.impl.event.network.PacketReceiveEvent;
import vip.allureclient.impl.event.player.UpdatePositionEvent;
import vip.allureclient.impl.property.ValueProperty;

@ModuleData(moduleName = "Atmosphere", keyBind = 0, category = ModuleCategory.WORLD)
public class Atmosphere extends Module {

    private final ValueProperty<Long> timeProperty = new ValueProperty<>("Time", 0L, 0L, 20000L, this);

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;

    @EventListener
    EventConsumer<PacketReceiveEvent> onPacketReceiveEvent;

    public Atmosphere(){
        onUpdatePositionEvent = (updatePositionEvent -> Wrapper.getWorld().setWorldTime(timeProperty.getPropertyValue()));

        onPacketReceiveEvent = (packetReceiveEvent -> {
           if (packetReceiveEvent.getPacket() instanceof S03PacketTimeUpdate) {
               packetReceiveEvent.setCancelled(true);
           }
        });
    }
}
