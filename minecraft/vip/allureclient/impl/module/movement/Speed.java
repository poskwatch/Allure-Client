package vip.allureclient.impl.module.movement;

import io.github.poskwatch.eventbus.api.annotations.EventHandler;
import io.github.poskwatch.eventbus.api.enums.Priority;
import io.github.poskwatch.eventbus.api.interfaces.IEventCallable;
import io.github.poskwatch.eventbus.api.interfaces.IEventListener;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import vip.allureclient.AllureClient;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.util.client.FlagCache;
import vip.allureclient.base.util.player.MovementUtil;
import vip.allureclient.base.util.visual.ChatUtil;
import vip.allureclient.impl.event.events.network.PacketReceiveEvent;
import vip.allureclient.impl.event.events.player.PlayerMoveEvent;
import vip.allureclient.impl.event.events.player.UpdatePositionEvent;
import vip.allureclient.impl.property.BooleanProperty;
import vip.allureclient.impl.property.ValueProperty;

import java.util.function.Supplier;

public class Speed extends Module {

    private final BooleanProperty lowHopProperty = new BooleanProperty("Reduce Y-Motion", true, this);

    private final ValueProperty<Float> timerBoostProperty = new ValueProperty<>("Timer Boost", 1.15F, 1.0F, 2.0F,  this);

    // Some encapsulation of if player should low-hop (reduce Y motion)
    private final Supplier<Boolean> shouldReduceSupplier = () -> {
            return mc.thePlayer.fallDistance < 4
                    && !MovementUtil.isOverVoid()
                    && !mc.thePlayer.isCollidedHorizontally
                    && mc.thePlayer.isMoving()
                    && lowHopProperty.getPropertyValue();
    };

    private boolean antiFlagState;


    public Speed() {
        super("Speed", ModuleCategory.MOVEMENT);
        setModuleSuffix("Watchdog");
        this.setListener(new IEventListener() {
            @EventHandler(events = UpdatePositionEvent.class, priority = Priority.VERY_HIGH)
            final IEventCallable<UpdatePositionEvent> onUpdatePosition = (event -> {
                if (MovementUtil.isMoving() && !mc.thePlayer.isSneaking() && event.isPre()) {
                    mc.timer.timerSpeed = timerBoostProperty.getPropertyValue();
                    if (antiFlagState) {
                        event.setY(event.getY() + 0.001);
                        event.setOnGround(false);
                        ChatUtil.sendMessageToPlayer("Tick");
                    }
                    else
                        event.setOnGround(true);
                    antiFlagState = !antiFlagState;
                }
            });
            @EventHandler(events = PlayerMoveEvent.class, priority = Priority.VERY_HIGH)
            final IEventCallable<PlayerMoveEvent> onPlayerMove = (event -> {
                if (event.isMoving() && !mc.thePlayer.isSneaking()) {
                    event.setSpeed(mc.thePlayer.onGround ? MovementUtil.getBaseMoveSpeed() * 1.51138 :
                            ((mc.thePlayer.motionY > 0 ? MovementUtil.getBaseMoveSpeed() * 1.26 : MovementUtil.getBaseMoveSpeed())));
                    if (mc.thePlayer.onGround && event.isMoving()) {
                        // Jump, reduce based on property
                        if (shouldReduceSupplier.get()) {
                            event.setY(0.36);
                            mc.thePlayer.motionY = 0.36;
                        }
                        else {
                            event.setY(0.42);
                            mc.thePlayer.motionY = 0.42;
                        }
                    }
                    if (shouldReduceSupplier.get()) {
                        if (event.getY() < 0)
                            event.setY(event.getY() - 0.15);
                        if (event.getY() > 0)
                            event.setY(mc.thePlayer.motionY -= 0.005);
                    }

                    if (AllureClient.getInstance().getModuleManager().getModuleOrNull("Scaffold").isToggled()) {
                        mc.thePlayer.setSprinting(false);
                    }
                }
            });
            @EventHandler(events = PacketReceiveEvent.class, priority = Priority.VERY_HIGH)
            final IEventCallable<PacketReceiveEvent> onReceivePacket = (event -> {
                final Packet<?> receivedPacket = event.getPacket();
                if (receivedPacket instanceof S08PacketPlayerPosLook) {
                    S08PacketPlayerPosLook receivedS08 = (S08PacketPlayerPosLook) receivedPacket;
                    if (FlagCache.recentFlag().getTimeStamp() == System.currentTimeMillis()) {
                        MovementUtil.setSpeed(0.0D);
                    }
                }
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
        mc.timer.timerSpeed = 1.0F;
        mc.thePlayer.motionX = 0;
        mc.thePlayer.motionZ = 0;
        super.onDisable();
    }
}
