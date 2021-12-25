package vip.allureclient.impl.module.player;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.module.annotations.ModuleData;
import vip.allureclient.base.util.client.TimerUtil;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.impl.event.network.PacketReceiveEvent;
import vip.allureclient.impl.event.network.PacketSendEvent;
import vip.allureclient.impl.event.player.UpdatePositionEvent;
import vip.allureclient.impl.property.EnumProperty;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

@ModuleData(moduleName = "Disabler", moduleBind = 0, moduleCategory = ModuleCategory.PLAYER)
public class Disabler extends Module {

    private final EnumProperty<DisablerMode> disablerModeProperty = new EnumProperty<>("Disabler Mode", DisablerMode.Watchdog, this);

    @EventListener
    EventConsumer<PacketReceiveEvent> onPacketReceiveEvent;

    @EventListener
    EventConsumer<PacketSendEvent> onPacketSendEvent;

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;

    private final Queue<Packet<?>> blocksMcPacketQueue = new ConcurrentLinkedDeque<>();
    private final TimerUtil blocksMcTimerUtil = new TimerUtil();
    private boolean expectedBlocksMCTeleport;

    public Disabler() {
        this.onPacketReceiveEvent = (event -> {
            if (event.getPacket() instanceof S08PacketPlayerPosLook) {
                if (Wrapper.getPlayer().ticksExisted < 100 && disablerModeProperty.getPropertyValue().equals(DisablerMode.Watchdog))
                    event.setCancelled(true);
                if (disablerModeProperty.getPropertyValue().equals(DisablerMode.BlocksMC) && expectedBlocksMCTeleport) {
                    S08PacketPlayerPosLook s08PacketPlayerPosLook = (S08PacketPlayerPosLook) event.getPacket();
                    event.setCancelled(true);
                    Wrapper.sendPacketDirect(new C03PacketPlayer.C06PacketPlayerPosLook(
                            s08PacketPlayerPosLook.getX(), s08PacketPlayerPosLook.getY(), s08PacketPlayerPosLook.getZ(),
                            s08PacketPlayerPosLook.getYaw(), s08PacketPlayerPosLook.getPitch(), true));
                }
            }
        });
        this.onUpdatePositionEvent = (event -> {
            if (disablerModeProperty.getPropertyValue().equals(DisablerMode.BlocksMC)) {
                if (!(Wrapper.getPlayer().ticksExisted > 50)) {
                    expectedBlocksMCTeleport = false;
                    blocksMcTimerUtil.reset();
                    blocksMcPacketQueue.clear();
                    return;
                }
                if (this.blocksMcTimerUtil.hasReached(260L) && disablerModeProperty.getPropertyValue().equals(DisablerMode.BlocksMC)) {
                    this.blocksMcTimerUtil.reset();
                    if (!blocksMcPacketQueue.isEmpty())
                        Wrapper.sendPacketDirect(blocksMcPacketQueue.poll());
                }
            }
        });
        this.onPacketSendEvent = (event -> {
           if (disablerModeProperty.getPropertyValue().equals(DisablerMode.BlocksMC)) {
               if (event.getPacket() instanceof C0FPacketConfirmTransaction ||
                    event.getPacket() instanceof C00PacketKeepAlive) {
                   short action = -1;
                   if (event.getPacket() instanceof C0FPacketConfirmTransaction)
                       action = ((C0FPacketConfirmTransaction) event.getPacket()).getUid();
                   if (action > 0 && action < 100)
                       return;
                   event.setCancelled(true);
                   this.blocksMcPacketQueue.add(event.getPacket());
               }
               if (event.getPacket() instanceof C03PacketPlayer) {
                   C03PacketPlayer c03PacketPlayer = (C03PacketPlayer) event.getPacket();
                   if (Wrapper.getPlayer().ticksExisted % 25 != 0)
                       return;
                   this.expectedBlocksMCTeleport = true;
                   c03PacketPlayer.setOnGround(false);
                   c03PacketPlayer.setY(-0.015625);
               }

           }
        });
    }

    private enum DisablerMode {
        Watchdog,
        BlocksMC
    }
}
