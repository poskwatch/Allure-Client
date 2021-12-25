package vip.allureclient.impl.module.player;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.module.annotations.ModuleData;
import vip.allureclient.base.util.client.TimerUtil;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.base.util.player.MovementUtil;
import vip.allureclient.impl.event.network.PacketSendEvent;
import vip.allureclient.impl.event.player.UpdatePositionEvent;
import vip.allureclient.impl.property.ValueProperty;

import java.util.ArrayList;

@ModuleData(moduleName = "Ping Spoof", moduleBind = 0, moduleCategory = ModuleCategory.PLAYER)
public class PingSpoof extends Module {

    private final ArrayList<Packet<?>> delayedPackets = new ArrayList<>();

    @EventListener
    EventConsumer<PacketSendEvent> onPacketSendEvent;

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;

    private final ValueProperty<Integer> packetDelayProperty = new ValueProperty<>("Delay (S)", 10, 0, 60, this);

    private final TimerUtil packetDelayTimer = new TimerUtil();

    public PingSpoof() {
        onPacketSendEvent = (packetSendEvent -> {
            if (packetSendEvent.getPacket() instanceof C0FPacketConfirmTransaction) {
                //delayedPackets.add(packetSendEvent.getPacket());
                //packetSendEvent.setCancelled(true);
            }
            if (packetSendEvent.getPacket() instanceof C03PacketPlayer.C06PacketPlayerPosLook ||
            packetSendEvent.getPacket() instanceof C03PacketPlayer.C05PacketPlayerLook) {
                packetSendEvent.setPacket(new C03PacketPlayer.C06PacketPlayerPosLook(
                        Wrapper.getPlayer().posX, Wrapper.getPlayer().posY, Wrapper.getPlayer().posZ, MovementUtil.getMovementDirection(),
                        Wrapper.getPlayer().rotationPitch, Wrapper.getPlayer().onGround));
            }
        });
        onUpdatePositionEvent = (updatePositionEvent -> {
           if (packetDelayTimer.hasReached(packetDelayProperty.getPropertyValue() * 1000)) {
               sendDelayedPackets();
               packetDelayTimer.reset();
           }
        });
    }

    @Override
    public void onEnable() {
        delayedPackets.clear();
        packetDelayTimer.reset();
    }

    private void sendDelayedPackets() {
        delayedPackets.forEach(Wrapper::sendPacketDirect);
        delayedPackets.clear();
    }
}
