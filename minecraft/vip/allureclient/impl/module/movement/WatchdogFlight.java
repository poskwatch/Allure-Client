package vip.allureclient.impl.module.movement;

import io.github.poskwatch.eventbus.api.annotations.EventHandler;
import io.github.poskwatch.eventbus.api.interfaces.IEventCallable;
import io.github.poskwatch.eventbus.api.interfaces.IEventListener;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.util.player.MovementUtil;
import vip.allureclient.base.util.player.PacketUtil;
import vip.allureclient.impl.event.events.network.PacketReceiveEvent;
import vip.allureclient.impl.event.events.network.PacketSendEvent;
import vip.allureclient.impl.event.events.player.PlayerMoveEvent;
import vip.allureclient.impl.event.events.player.UpdatePositionEvent;

public class WatchdogFlight extends Module {

    private int flightStage;

    public WatchdogFlight() {
        super("Watchdog Flight", ModuleCategory.MOVEMENT);
        this.setListener(new IEventListener() {
            @EventHandler(events = UpdatePositionEvent.class)
            final IEventCallable<UpdatePositionEvent> onUpdatePosition = (event -> {
                switch (flightStage) {
                    case 0:
                        for (int i = 0; i < 10; i++) {
                            PacketUtil.sendPacketDirect(new C03PacketPlayer.C04PacketPlayerPosition(
                                    event.getX(), event.getY() - 1.0 + (i / 10.0D), event.getZ(), false
                            ));
                        }
                        flightStage = 1;
                        break;
                    case 1:
                        event.setY(event.getY() - 0.75D);
                        break;
                    case 2:
                        mc.thePlayer.motionY = 0;
                        double[] xz = {
                                -Math.sin(Math.toRadians(event.getYaw())) * MovementUtil.getBaseMoveSpeed(),
                                Math.cos(Math.toRadians(event.getYaw())) * MovementUtil.getBaseMoveSpeed()
                        };
                        PacketUtil.sendPacketDirect(new C03PacketPlayer.C04PacketPlayerPosition(
                                event.getX() - xz[0], event.getY(), event.getZ() - xz[1], true
                        ));
                        mc.thePlayer.onGround = true;
                        mc.thePlayer.setPosition(
                                mc.thePlayer.posX + xz[0] * 0.1,  mc.thePlayer.posY, mc.thePlayer.posZ + xz[1] * 0.1
                        );
                }

            });
            @EventHandler(events = PlayerMoveEvent.class)
            final IEventCallable<PlayerMoveEvent> onPlayerMove = (event -> {
                if (flightStage == 2)
                    event.setSpeed(MovementUtil.getBaseMoveSpeed() * 0.6);
                else
                    event.setCancelled();
            });
            @EventHandler(events = PacketSendEvent.class)
            final IEventCallable<PacketSendEvent> onPacketSend = (event -> {
                final Packet<?> receivedPacket = event.getPacket();
                if (receivedPacket instanceof C03PacketPlayer && mc.thePlayer.ticksExisted % 2 == 0) {
                    event.setCancelled();
                }
            });
            @EventHandler(events = PacketReceiveEvent.class)
            final IEventCallable<PacketReceiveEvent> onPacketReceive = (event -> {
                final Packet<?> receivedPacket = event.getPacket();
                // Initial flag from clip
                if (flightStage == 1 && receivedPacket instanceof S08PacketPlayerPosLook) {
                    S08PacketPlayerPosLook receivedS08 = (S08PacketPlayerPosLook) receivedPacket;
                    // Reverse clip
                    event.setCancelled();
                    for (int i = 0; i < 10; i++) {
                        PacketUtil.sendPacketDirect(new C03PacketPlayer.C04PacketPlayerPosition(
                                mc.thePlayer.posX, mc.thePlayer.posY + 1.0 + (i / 10.0D), mc.thePlayer.posZ, true
                        ));
                    }
                    // Proceed with flight
                    flightStage = 2;
                }
            });
        });
    }

    @Override
    public void onEnable() {
        // Restart flight cycle, setting stage to 0
        this.flightStage = 0;
        C13PacketPlayerAbilities c13 = new C13PacketPlayerAbilities();
        c13.setCreativeMode(true);
        c13.setAllowFlying(true);
        c13.setFlying(true);
        mc.thePlayer.jump();
        PacketUtil.sendPacketDirect(c13);
        c13.setCreativeMode(false);
        c13.setAllowFlying(false);
        c13.setFlying(false);
        PacketUtil.sendPacketDirect(c13);
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
