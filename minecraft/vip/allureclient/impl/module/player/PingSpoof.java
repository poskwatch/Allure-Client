package vip.allureclient.impl.module.player;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.event.Listener;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.ModuleCategory;
import vip.allureclient.base.module.ModuleData;
import vip.allureclient.base.util.client.TimerUtil;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.base.util.visual.ChatUtil;
import vip.allureclient.impl.event.network.PacketSendEvent;
import vip.allureclient.impl.event.player.UpdatePositionEvent;
import vip.allureclient.impl.property.ValueProperty;

import java.util.ArrayList;

@ModuleData(moduleName = "PingSpoof", keyBind = 0, category = ModuleCategory.PLAYER)
public class PingSpoof extends Module {

    private final ArrayList<Packet> delayedPackets = new ArrayList<>();

    @EventListener
    Listener<PacketSendEvent> onPacketSendEvent;

    @EventListener
    Listener<UpdatePositionEvent> onUpdatePositionEvent;

    private final ValueProperty<Integer> packetDelayProperty = new ValueProperty<>("Delay (S)", 10, 0, 60, this);

    private final TimerUtil packetDelayTimer = new TimerUtil();

    public PingSpoof() {
        onModuleEnabled = () -> {
            delayedPackets.clear();
            packetDelayTimer.reset();
        };

        onPacketSendEvent = (packetSendEvent -> {
            if (packetSendEvent.getPacket() instanceof C00PacketKeepAlive) {
                delayedPackets.add(packetSendEvent.getPacket());
                packetSendEvent.setCancelled(true);
            }
        });

        onUpdatePositionEvent = (updatePositionEvent -> {
           if (packetDelayTimer.hasReached(packetDelayProperty.getPropertyValue() * 1000)) {
               sendDelayedPackets();
               packetDelayTimer.reset();
           }
        });
    }

    private void sendDelayedPackets() {
        delayedPackets.forEach(Wrapper::sendPacketDirect);
        delayedPackets.clear();
    }

}
