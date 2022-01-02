package vip.allureclient.impl.module.movement;

import net.minecraft.network.play.client.C03PacketPlayer;
import org.lwjgl.input.Keyboard;
import vip.allureclient.AllureClient;
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
import vip.allureclient.visual.notification.NotificationType;

@ModuleData(moduleName = "Flight", moduleBind = Keyboard.KEY_G, moduleCategory = ModuleCategory.MOVEMENT)
public class Flight extends Module {

    private int groundTicks;

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
                       double dist = 7.3;
                       double x = Wrapper.getPlayer().posX;
                       double y = Wrapper.getPlayer().posY;
                       double z = Wrapper.getPlayer().posZ;
                       if (Wrapper.getPlayer().ticksExisted % 6 == 0)
                       Wrapper.sendPacketDirect(new C03PacketPlayer.C04PacketPlayerPosition(x + (-Math.sin(Math.toRadians(yaw)) * dist), y - 1.75, z + (Math.cos(Math.toRadians(yaw)) * dist), Wrapper.getPlayer().onGround));
                   }
                   break;
               case Watchmeme:

                   Wrapper.getPlayer().posY -= Wrapper.getPlayer().posY - Wrapper.getPlayer().lastTickPosY;
                   Wrapper.getPlayer().lastTickPosY -= Wrapper.getPlayer().posY - Wrapper.getPlayer().lastTickPosY;


                   if (MovementUtil.isMoving()) {
                       if (Wrapper.getPlayer().onGround) {
                           MovementUtil.setSpeed(MovementUtil.getBaseMoveSpeed() * 2);
                           Wrapper.getPlayer().jump();
                       }
                       if (Wrapper.getPlayer().motionY <= 0)
                           MovementUtil.setSpeed(MovementUtil.getBaseMoveSpeed() * 0.8);
                   }
                   if (Wrapper.getPlayer().onGround && groundTicks++ > 2) {
                       AllureClient.getInstance().getNotificationManager().addNotification("Flight toggled",
                               "Flight was automatically toggled to prevent lagbacks", 2500, NotificationType.WARNING);
                       setToggled(false);
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
            if (flightModeProperty.getPropertyValue().equals(flightModes.Watchdog)) {
                playerMoveEvent.setCancelled(true);
            }
            else if (!flightModeProperty.getPropertyValue().equals(flightModes.Watchmeme))
                playerMoveEvent.setSpeed(flightSpeedProperty.getPropertyValue());
        });
    }

    @Override
    public void onEnable() {
        groundTicks = 0;
        Wrapper.getPlayer().motionX = 0;
        Wrapper.getPlayer().motionZ = 0;
        Wrapper.getPlayer().motionY = 0;
        super.onEnable();
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
    EventConsumer<PlayerMoveEvent> onPlayerMoveEvent;

    EnumProperty<flightModes> flightModeProperty = new EnumProperty<>("Flight Mode", flightModes.Vanilla, this);

    ValueProperty<Double> flightSpeedProperty = new ValueProperty<>("Flight Speed", 0.5D, 0.1D, 2D, this);

    BooleanProperty chokePacketsProperty = new BooleanProperty("Choke Packets", true, this);

    enum flightModes {
        Vanilla,
        Watchdog,
        Watchmeme
    }

}
