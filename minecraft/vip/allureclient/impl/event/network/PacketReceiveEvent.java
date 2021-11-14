package vip.allureclient.impl.event.network;

import net.minecraft.network.Packet;
import vip.allureclient.base.event.CancellableEvent;

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
