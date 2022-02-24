package vip.allureclient.impl.module.movement;

import io.github.poskwatch.eventbus.api.annotations.EventHandler;
import io.github.poskwatch.eventbus.api.enums.Priority;
import io.github.poskwatch.eventbus.api.interfaces.IEventCallable;
import io.github.poskwatch.eventbus.api.interfaces.IEventListener;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import org.lwjgl.input.Keyboard;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.util.player.MovementUtil;
import vip.allureclient.base.util.player.PacketUtil;
import vip.allureclient.impl.event.events.network.PacketReceiveEvent;
import vip.allureclient.impl.event.events.network.PacketSendEvent;
import vip.allureclient.impl.event.events.player.PlayerMoveEvent;
import vip.allureclient.impl.event.events.player.UpdatePositionEvent;
import vip.allureclient.impl.property.BooleanProperty;
import vip.allureclient.impl.property.EnumProperty;
import vip.allureclient.impl.property.ValueProperty;

public class Flight extends Module {

    private double originalY;
    private int watchdogStage;

    double[] watchdogClipOffsets = {
            0.41999998688698,
            0.7531999805212,
            1.00133597911214,
            1.16610926093821,
            1.24918707874468,
            1.24918707874468,
            1.1707870772188,
            1.0155550727022,
            0.78502770378923,
            0.48071087633169,
            0.10408037809304
    };

    public Flight() {
        super("Flight", Keyboard.KEY_G, ModuleCategory.MOVEMENT);
        this.setListener(new IEventListener() {
            @EventHandler(events = UpdatePositionEvent.class, priority = Priority.VERY_HIGH)
            final IEventCallable<UpdatePositionEvent> onUpdatePosition = (event -> {
                if (event.isPre()) {
                    switch (flightModeProperty.getPropertyValue()) {
                        case VANILLA:
                            mc.thePlayer.motionY = 0;
                            if (mc.gameSettings.keyBindJump.isKeyDown())
                                mc.thePlayer.motionY = 0.5d;
                            if (mc.gameSettings.keyBindSneak.isKeyDown())
                                mc.thePlayer.motionY = -0.5d;
                            break;
                        case WATCHDOG:
                            if (watchdogStage == 0) {
                                for (double doubleValue : watchdogClipOffsets) {
                                    PacketUtil.sendPacketDirect(new C03PacketPlayer.C04PacketPlayerPosition(event.getX(),
                                            event.getY() + doubleValue, event.getZ(), false));
                                }
                                watchdogStage = 1;
                            }
                            if (watchdogStage == 1) {
                                mc.timer.timerSpeed = 1;
                                event.setY(event.getY() - 0.42);
                                watchdogStage = 1;
                            } else if (watchdogStage >= 2) {
                                event.setY(originalY);
                                mc.thePlayer.motionY = 0;
                                watchdogStage++;
                            }
                            break;
                    }
                    setModuleSuffix(flightModeProperty.getEnumValueAsString());
                }
            });
            @EventHandler(events = PlayerMoveEvent.class, priority = Priority.HIGH)
            final IEventCallable<PlayerMoveEvent> onPlayerMove = (event -> {
                if (flightModeProperty.getPropertyValue().equals(FlightMode.VANILLA))
                    event.setSpeed(flightSpeedProperty.getPropertyValue());
                if (flightModeProperty.getPropertyValue().equals(FlightMode.WATCHDOG)) {
                    event.setSpeed(watchdogStage >= 2 ? MovementUtil.getBaseMoveSpeed() : 0);
                }
            });
            @EventHandler(events = PacketReceiveEvent.class)
            final IEventCallable<PacketReceiveEvent> onPacketReception = (event -> {
                if (event.getPacket() instanceof S08PacketPlayerPosLook && watchdogStage == 1) {
                    S08PacketPlayerPosLook s08PacketPlayerPosLook = (S08PacketPlayerPosLook) event.getPacket();
                    originalY = s08PacketPlayerPosLook.getY();
                    PacketUtil.sendPacketDirect((new C03PacketPlayer.C04PacketPlayerPosition(
                            s08PacketPlayerPosLook.getX(), s08PacketPlayerPosLook.getY(), s08PacketPlayerPosLook.getZ(), false)));
                    watchdogStage = 2;
                    event.setCancelled(true);
                }
            });
            @EventHandler(events = PacketSendEvent.class)
            final IEventCallable<PacketSendEvent> onSendPacket = (event -> {
                if (chokePacketsProperty.getPropertyValue()) {
                    if (event.getPacket() instanceof C03PacketPlayer) {
                        event.setCancelled(true);
                    }
                }
            });
        });
    }

    @Override
    public void onEnable() {
        this.watchdogStage = 0;
        if (!flightModeProperty.getPropertyValue().equals(FlightMode.WATCHDOG))
            mc.thePlayer.jump();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1;
        super.onDisable();
    }

    EnumProperty<FlightMode> flightModeProperty = new EnumProperty<>("Flight Mode", FlightMode.VANILLA, this);

    ValueProperty<Double> flightSpeedProperty = new ValueProperty<>("Flight Speed", 0.5D, 0.1D, 2D, this);

    BooleanProperty chokePacketsProperty = new BooleanProperty("Choke Packets", true, this);

    enum FlightMode {
        VANILLA("Vanilla"),
        WATCHDOG("Watchdog");
        private final String name;

        FlightMode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
