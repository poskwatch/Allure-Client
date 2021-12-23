package vip.allureclient.impl.module.movement;

import net.minecraft.network.play.client.C03PacketPlayer;
import org.lwjgl.input.Keyboard;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.module.annotations.ModuleData;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.base.util.player.MovementUtil;
import vip.allureclient.impl.event.network.PacketSendEvent;
import vip.allureclient.impl.event.player.PlayerMoveEvent;
import vip.allureclient.impl.event.player.UpdatePositionEvent;
import vip.allureclient.impl.property.BooleanProperty;
import vip.allureclient.impl.property.EnumProperty;
import vip.allureclient.impl.property.ValueProperty;

@ModuleData(moduleName = "Flight", moduleBind = Keyboard.KEY_G, moduleCategory = ModuleCategory.MOVEMENT)
public class Flight extends Module {

    public Flight() {
        onUpdatePositionEvent = (updatePositionEvent -> {
           switch (flightModeProperty.getPropertyValue()){
               case Vanilla:
                   Wrapper.getPlayer().cameraYaw = 0.1f;
                   Wrapper.getPlayer().motionY = 0;
                   if (Wrapper.getMinecraft().gameSettings.keyBindJump.isKeyDown())
                       Wrapper.getPlayer().motionY = 0.5d;
                   if (Wrapper.getMinecraft().gameSettings.keyBindSneak.isKeyDown())
                       Wrapper.getPlayer().motionY = -0.5d;
                   break;
               case Watchdog:
                   Wrapper.getPlayer().motionY = 0.0D;
                   if(MovementUtil.isMoving()) {
                       float yaw = Wrapper.getPlayer().rotationYaw;
                       double dist = 7.9;
                       double x = Wrapper.getPlayer().posX;
                       double y = Wrapper.getPlayer().posY;
                       double z = Wrapper.getPlayer().posZ;
                       if (Wrapper.getPlayer().ticksExisted % 7 == 0)
                       Wrapper.sendPacketDirect(new C03PacketPlayer.C04PacketPlayerPosition(x + (-Math.sin(Math.toRadians(yaw)) * dist), y - 1.75, z + (Math.cos(Math.toRadians(yaw)) * dist), Wrapper.getPlayer().onGround));
                   }

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

            if(!playerMoveEvent.isMoving())
                playerMoveEvent.setSpeed(0);
            if (flightModeProperty.getPropertyValue().equals(flightModes.Watchdog) && Wrapper.getPlayer().ticksExisted % 4 != 0) {
               // playerMoveEvent.setCancelled(true);
            }
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
