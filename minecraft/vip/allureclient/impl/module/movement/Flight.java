package vip.allureclient.impl.module.movement;

import net.minecraft.network.play.client.C03PacketPlayer;
import org.lwjgl.input.Keyboard;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.ModuleCategory;
import vip.allureclient.base.module.ModuleData;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.impl.event.network.PacketSendEvent;
import vip.allureclient.impl.event.player.PlayerMoveEvent;
import vip.allureclient.impl.event.player.UpdatePositionEvent;
import vip.allureclient.impl.property.BooleanProperty;
import vip.allureclient.impl.property.EnumProperty;
import vip.allureclient.impl.property.ValueProperty;

@ModuleData(moduleName = "Flight", keyBind = Keyboard.KEY_G, category = ModuleCategory.MOVEMENT)
public class Flight extends Module {

    public Flight() {
        onUpdatePositionEvent = (updatePositionEvent -> {
           switch (flightModeProperty.getPropertyValue()){
               case Vanilla:
                   Wrapper.getPlayer().motionY = 0;
                   break;
               case Watchdog:
                   Wrapper.getPlayer().motionY = 0.1;
                   break;
           }
           setModuleSuffix(flightModeProperty.getEnumValueAsString());
        });
        onPacketSendEvent = (packetSendEvent -> {
            if (chokePacketsProperty.getPropertyValue()) {
                if (packetSendEvent.getPacket() instanceof C03PacketPlayer) {
                    packetSendEvent.setCancelled(true);
                }
            }
        });
        onPlayerMoveEvent = (playerMoveEvent -> {
            playerMoveEvent.setSpeed(flightSpeedProperty.getPropertyValue());
            if(!playerMoveEvent.isMoving())
                playerMoveEvent.setSpeed(0);
        });
    }

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;

    @EventListener
    EventConsumer<PacketSendEvent> onPacketSendEvent;

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
