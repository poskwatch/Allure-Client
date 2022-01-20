package vip.allureclient.impl.module.movement;

import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import org.lwjgl.input.Keyboard;
import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.annotations.ModuleData;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.base.util.player.MovementUtil;
import vip.allureclient.impl.event.network.PacketReceiveEvent;
import vip.allureclient.impl.event.network.PacketSendEvent;
import vip.allureclient.impl.event.player.PlayerMoveEvent;
import vip.allureclient.impl.event.player.UpdatePositionEvent;
import vip.allureclient.impl.property.BooleanProperty;
import vip.allureclient.impl.property.EnumProperty;
import vip.allureclient.impl.property.ValueProperty;

@ModuleData(moduleName = "Flight", moduleBind = Keyboard.KEY_G, moduleCategory = ModuleCategory.MOVEMENT)
public class Flight extends Module {

    private double originalY;
    private int watchdogStage;

    // TODO: Improve (Can't fly that long...)

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
        onUpdatePositionEvent = (updatePositionEvent -> {
            if (updatePositionEvent.isPre()) {
                switch (flightModeProperty.getPropertyValue()) {
                    case Vanilla:
                        Wrapper.getPlayer().cameraYaw = 0.1f;
                        Wrapper.getPlayer().motionY = 0;
                        if (Wrapper.getMinecraft().gameSettings.keyBindJump.isKeyDown())
                            Wrapper.getPlayer().motionY = 0.5d;
                        if (Wrapper.getMinecraft().gameSettings.keyBindSneak.isKeyDown())
                            Wrapper.getPlayer().motionY = -0.5d;
                        break;
                    case Watchdog:
                        if (watchdogStage == 0) {
                            for (double doubleValue : this.watchdogClipOffsets) {
                                Wrapper.sendPacketDirect(new C03PacketPlayer.C04PacketPlayerPosition(updatePositionEvent.getX(),
                                        updatePositionEvent.getY() + doubleValue, updatePositionEvent.getZ(), false));
                            }
                            watchdogStage = 1;
                        }
                        if (this.watchdogStage == 1) {
                            Wrapper.getMinecraft().timer.timerSpeed = 1;
                            updatePositionEvent.setY(updatePositionEvent.getY() - 0.42);
                            watchdogStage = 1;
                        }
                        else if (watchdogStage >= 2) {
                            updatePositionEvent.setY(originalY);
                            Wrapper.getPlayer().motionY = 0;
                            watchdogStage++;
                        }
                        break;
                }
                Wrapper.getPlayer().cameraYaw = 0.1f;
                setModuleSuffix(flightModeProperty.getEnumValueAsString());
            }
        });
        this.onPacketReceiveEvent = (packetReceiveEvent -> {
           if (packetReceiveEvent.getPacket() instanceof S08PacketPlayerPosLook && watchdogStage == 1) {
               S08PacketPlayerPosLook s08PacketPlayerPosLook = (S08PacketPlayerPosLook) packetReceiveEvent.getPacket();
               this.originalY = s08PacketPlayerPosLook.getY();
               Wrapper.sendPacketDirect((new C03PacketPlayer.C04PacketPlayerPosition(
                       s08PacketPlayerPosLook.getX(), s08PacketPlayerPosLook.getY(), s08PacketPlayerPosLook.getZ(), false)));
               this.watchdogStage = 2;
               packetReceiveEvent.setCancelled(true);
           }
        });
        onPacketSendEvent = (packetSendEvent -> {
            if (chokePacketsProperty.getPropertyValue()) {
                if (packetSendEvent.getPacket() instanceof C03PacketPlayer) {
                    packetSendEvent.setCancelled(true);
                }
            }
        });
        onPlayerMoveEvent = (playerMoveEvent -> {
            if (flightModeProperty.getPropertyValue().equals(flightModes.Vanilla))
                playerMoveEvent.setSpeed(flightSpeedProperty.getPropertyValue());
            if (flightModeProperty.getPropertyValue().equals(flightModes.Watchdog)) {
                    playerMoveEvent.setSpeed(watchdogStage >= 2 ? MovementUtil.getBaseMoveSpeed() : 0);
            }
        });
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.watchdogStage = 0;
    }

    @Override
    public void onDisable() {
        Wrapper.getMinecraft().timer.timerSpeed = 1;
        super.onDisable();
    }

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;

    @EventListener
    EventConsumer<PacketSendEvent> onPacketSendEvent;

    @EventListener
    EventConsumer<PacketReceiveEvent> onPacketReceiveEvent;

    @EventListener
    EventConsumer<PlayerMoveEvent> onPlayerMoveEvent;

    EnumProperty<flightModes> flightModeProperty = new EnumProperty<>("Flight Mode", flightModes.Vanilla, this);

    ValueProperty<Double> flightSpeedProperty = new ValueProperty<>("Flight Speed", 0.5D, 0.1D, 2D, this);

    BooleanProperty chokePacketsProperty = new BooleanProperty("Choke Packets", true, this);

    enum flightModes {
        Vanilla,
        Watchdog
    }

}
