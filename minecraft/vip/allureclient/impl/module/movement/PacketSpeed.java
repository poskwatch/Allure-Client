package vip.allureclient.impl.module.movement;

import io.github.poskwatch.eventbus.api.annotations.EventHandler;
import io.github.poskwatch.eventbus.api.interfaces.IEventCallable;
import io.github.poskwatch.eventbus.api.interfaces.IEventListener;
import net.minecraft.network.play.client.C03PacketPlayer;
import vip.allureclient.AllureClient;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.util.player.MovementUtil;
import vip.allureclient.base.util.player.PacketUtil;
import vip.allureclient.impl.event.events.player.PlayerMoveEvent;
import vip.allureclient.impl.event.events.player.UpdatePositionEvent;

import java.util.ArrayList;
import java.util.function.Consumer;

public class PacketSpeed extends Module {

    // Basic packet speed, allowing faster movement without flagging

    public PacketSpeed() {
        super("Packet Speed", ModuleCategory.MOVEMENT);
        this.setListener(new IEventListener() {
            @EventHandler(events = UpdatePositionEvent.class)
            final IEventCallable<UpdatePositionEvent> onUpdatePosition = (event -> {
                double[] xZCalculations = {
                        -Math.sin(Math.toRadians(MovementUtil.getMovementDirection())) * (MovementUtil.getBaseMoveSpeed()),
                        Math.cos(Math.toRadians(MovementUtil.getMovementDirection())) * (MovementUtil.getBaseMoveSpeed())
                };
                if (event.isPre()) {
                    if (mc.thePlayer.isMoving() && mc.thePlayer.onGround) {
                        PacketUtil.sendPacketDirect(new C03PacketPlayer.C04PacketPlayerPosition(
                                event.getX() - xZCalculations[0],
                                event.getY(),
                                event.getZ() - xZCalculations[1],
                                event.isOnGround()));
                    }
                }
            });
            @EventHandler(events = PlayerMoveEvent.class)
            final IEventCallable<PlayerMoveEvent> onPlayerMove = (event -> {
                if (mc.thePlayer.onGround)
                    event.setSpeed(MovementUtil.getBaseMoveSpeed() * 2.0D);
            });
        });
        // Register flag listener
        this.registerFlagListener();
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.timer.timerSpeed = 1.0f;
    }
}
