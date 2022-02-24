package vip.allureclient.impl.event.events.network;

import net.minecraft.network.Packet;
import vip.allureclient.impl.event.CancellableEvent;

public class PacketReceiveEvent extends CancellableEvent {

    private Packet<?> packet;

    public PacketReceiveEvent(Packet<?> packet){
        this.packet = packet;
    }

    public Packet<?> getPacket() {
        return packet;
    }

    public void setPacket(Packet<?> packet) {
        this.packet = packet;
    }
}
