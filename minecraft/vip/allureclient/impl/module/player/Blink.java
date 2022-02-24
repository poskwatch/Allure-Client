package vip.allureclient.impl.module.player;

import io.github.poskwatch.eventbus.api.annotations.EventHandler;
import io.github.poskwatch.eventbus.api.interfaces.IEventCallable;
import io.github.poskwatch.eventbus.api.interfaces.IEventListener;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import vip.allureclient.AllureClient;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.util.player.PacketUtil;
import vip.allureclient.impl.event.events.network.PacketSendEvent;
import vip.allureclient.impl.event.events.visual.Render2DEvent;
import vip.allureclient.impl.property.ValueProperty;

import java.util.LinkedList;
import java.util.Queue;

public class Blink extends Module {

    // Create queue for cancelled packets.
    private final Queue<Packet<?>> cancelledPacketQueue = new LinkedList<>();

    // Limit property to control the limit of packets able to be cancelled before flush
    private final ValueProperty<Integer> packetLimitProperty = new ValueProperty<>(
            "Packet Limit", 50, 1, 200, this);

    public Blink() {
        super("Blink", ModuleCategory.PLAYER);
        this.setListener(new IEventListener() {
            @EventHandler(events = PacketSendEvent.class)
            final IEventCallable<PacketSendEvent> onPacketSend = (event -> {
                final Packet<?> sentPacket = event.getPacket();
                // If it's a player packet, C0F, or C00, cancel and add to queue
                if (sentPacket instanceof C03PacketPlayer ||
                        sentPacket instanceof C0FPacketConfirmTransaction ||
                        sentPacket instanceof C00PacketKeepAlive) {
                    event.setCancelled();
                    cancelledPacketQueue.add(sentPacket);
                }
                // If cancelled packets have exceeded limit, flush
                if (cancelledPacketQueue.size() >= packetLimitProperty.getPropertyValue()) {
                    flushPacketQueue();
                }
            });
            @EventHandler(events = Render2DEvent.class)
            final IEventCallable<Render2DEvent> on2DRender = (event -> {
                // Render an indicator of cancelled packets
                final ScaledResolution sr = event.getScaledResolution();
                final int width = sr.getScaledWidth();
                final int height = sr.getScaledHeight();
                AllureClient.getInstance().getFontManager().csgoFontRenderer.drawCenteredString(
                        String.format("Cancelled %d Packets", cancelledPacketQueue.size()), width/2.0F, height/2.0F + 5.0F, 0xFFFFFFFF
                );
            });
        });
    }

    @Override
    public void onEnable() {
        // Clear queue on enable
        this.cancelledPacketQueue.clear();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        // When disabled, flush packet queue
        this.flushPacketQueue();
        super.onDisable();
    }

    // Method to flush packet queue
    private void flushPacketQueue() {
        while (!this.cancelledPacketQueue.isEmpty()) {
            PacketUtil.sendPacketDirect(this.cancelledPacketQueue.poll());
        }
    }
}
