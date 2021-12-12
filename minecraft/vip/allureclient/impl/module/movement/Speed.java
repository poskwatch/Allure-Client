package vip.allureclient.impl.module.movement;

import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import org.lwjgl.input.Keyboard;
import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.ModuleCategory;
import vip.allureclient.base.module.ModuleData;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.base.util.player.MovementUtil;
import vip.allureclient.base.util.visual.ChatUtil;
import vip.allureclient.impl.event.network.PacketReceiveEvent;
import vip.allureclient.impl.event.network.PacketSendEvent;
import vip.allureclient.impl.event.player.PlayerMoveEvent;
import vip.allureclient.impl.event.player.UpdatePositionEvent;
import vip.allureclient.impl.property.BooleanProperty;
import vip.allureclient.impl.property.EnumProperty;

@ModuleData(moduleName = "Speed", moduleBind = Keyboard.KEY_V, moduleCategory = ModuleCategory.MOVEMENT)
public class Speed extends Module {

    private double moveSpeed;
    private double lastDist;
    private int hopStage;

    private final EnumProperty<SpeedModes> speedModesProperty = new EnumProperty<>("Mode", SpeedModes.YPort, this);

    private final BooleanProperty glideProperty = new BooleanProperty("Glide Down", true, this);

    @EventListener
    EventConsumer<PlayerMoveEvent> onPlayerMoveEvent;

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;

    @EventListener
    EventConsumer<PacketSendEvent> onPacketSendEvent;

    @EventListener
    EventConsumer<PacketReceiveEvent> onPacketReceiveEvent;

    public Speed() {
        setModuleSuffix("Watchdog");
        this.onModuleEnabled = () -> moveSpeed = 0;
        this.onModuleDisabled = () -> {
            Wrapper.getPlayer().motionX = 0;
            Wrapper.getPlayer().motionZ = 0;
        };
        this.onPlayerMoveEvent = (playerMoveEvent -> {
            if(speedModesProperty.getPropertyValue().equals(SpeedModes.Strafe)) {
                final double MOVE_SPEED_MULTIPLIER = Wrapper.getPlayer().movementInput.moveStrafe > 0 ? 0.4 : 0.6;
                if (MovementUtil.isMoving() && Wrapper.getPlayer().onGround && hopStage == 1) {
                    moveSpeed = MOVE_SPEED_MULTIPLIER * MovementUtil.getBaseMoveSpeed();
                } else if (MovementUtil.isMoving() && Wrapper.getPlayer().onGround && hopStage == 2) {
                    moveSpeed = MOVE_SPEED_MULTIPLIER * MovementUtil.getBaseMoveSpeed();
                } else if (hopStage == 3) {
                    moveSpeed = MovementUtil.getBaseMoveSpeed() * MOVE_SPEED_MULTIPLIER * 1.3;
                } else {
                    if (MovementUtil.isOnGround(-Wrapper.getPlayer().motionY) || Wrapper.getPlayer().isCollidedVertically && Wrapper.getPlayer().onGround) {
                        hopStage = 1;
                    }
                }
                playerMoveEvent.setSpeed(moveSpeed);
                hopStage++;
            }
            else {
                if (Wrapper.getPlayer().motionY > 0 && Wrapper.getPlayer().fallDistance <= 1 && !Wrapper.getPlayer().isInWater()) {
                    playerMoveEvent.setY(Wrapper.getPlayer().motionY - 0.25);
                }
            }
        });
        this.onUpdatePositionEvent = (updatePositionEvent -> {
            if (updatePositionEvent.isPre()) {
                if (speedModesProperty.getPropertyValue().equals(SpeedModes.Strafe)) {
                    final double xDist = Wrapper.getPlayer().posX - Wrapper.getPlayer().prevPosX;
                    final double zDist = Wrapper.getPlayer().posZ - Wrapper.getPlayer().prevPosZ;
                    lastDist = Math.sqrt(xDist * xDist + zDist * zDist);
                    if (hopStage == 4 && MovementUtil.isMoving() && Wrapper.getPlayer().onGround) {
                        Wrapper.getPlayer().jump();
                    }
                    if (glideProperty.getPropertyValue() && Wrapper.getPlayer().motionY < 0) {
                        Wrapper.getPlayer().motionY += 0.015D;
                    }
                }
                else {
                    if (MovementUtil.isMoving() && Wrapper.getPlayer().onGround) {
                        Wrapper.getPlayer().jump();
                        Wrapper.getPlayer().setSpeed(0.278335f);
                    }
                }
            }
        });
        this.onPacketSendEvent = (packetSendEvent -> {
           if ((packetSendEvent.getPacket() instanceof C03PacketPlayer.C06PacketPlayerPosLook ||
                   packetSendEvent.getPacket() instanceof C03PacketPlayer.C04PacketPlayerPosition) && speedModesProperty.getPropertyValue().equals(SpeedModes.Strafe)
                    && !Wrapper.getPlayer().onGround && Wrapper.getPlayer().ticksExisted % 5 == 0) {
               ChatUtil.sendMessageToPlayer("\2474\247lC03 Balling");
               Wrapper.sendPacketDirect(new C03PacketPlayer.C04PacketPlayerPosition(Wrapper.getPlayer().posX, Wrapper.getPlayer().posY, Wrapper.getPlayer().posZ, true));
               packetSendEvent.setCancelled(true);
           }
        });
        this.onPacketReceiveEvent = (packetReceiveEvent -> {
            if (packetReceiveEvent.getPacket() instanceof S08PacketPlayerPosLook && speedModesProperty.getPropertyValue().equals(SpeedModes.Strafe)
                    && !Wrapper.getPlayer().onGround && Wrapper.getPlayer().ticksExisted % 2 == 0) {
                packetReceiveEvent.setCancelled(true);
            }
        });
    }

    private enum SpeedModes {
        Strafe,
        YPort
    }
}
