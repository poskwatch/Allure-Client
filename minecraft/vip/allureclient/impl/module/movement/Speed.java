package vip.allureclient.impl.module.movement;

import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.ModuleCategory;
import vip.allureclient.base.module.ModuleData;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.base.util.player.MovementUtil;
import vip.allureclient.impl.event.player.PlayerMoveEvent;
import vip.allureclient.impl.event.player.UpdatePositionEvent;
import vip.allureclient.impl.property.BooleanProperty;

@ModuleData(moduleName = "Speed", keyBind = Keyboard.KEY_V, category = ModuleCategory.MOVEMENT)
public class Speed extends Module {

    private double moveSpeed;
    private double lastDist;
    private int hopStage;

    private final BooleanProperty glideProperty = new BooleanProperty("Glide Down", true, this);

    @EventListener
    EventConsumer<PlayerMoveEvent> onPlayerMoveEvent;

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;

    public Speed() {
        setModuleSuffix("Watchdog");
        this.onModuleEnabled = () -> moveSpeed = 0;
        this.onModuleDisabled = () -> {
            Wrapper.getPlayer().motionX = 0;
            Wrapper.getPlayer().motionZ = 0;
        };
        this.onPlayerMoveEvent = (playerMoveEvent -> {
           if (MovementUtil.isMoving() && Wrapper.getPlayer().onGround && hopStage == 1) {
               moveSpeed = 1.1 * MovementUtil.getBaseMoveSpeed();
           }
           else if (MovementUtil.isMoving() && Wrapper.getPlayer().onGround && hopStage == 2) {
               moveSpeed = 1.1 * MovementUtil.getBaseMoveSpeed();
           }
           else if (hopStage == 3) {
               final double difference = 0.45 * (lastDist - MovementUtil.getBaseMoveSpeed()) - 1.0E-5;
               moveSpeed = lastDist - difference;
           }
           else {
               if(MovementUtil.isOnGround(-Wrapper.getPlayer().motionY) || Wrapper.getPlayer().isCollidedVertically && Wrapper.getPlayer().onGround) {
                   hopStage = 1;
               }
               moveSpeed = lastDist - lastDist / 90.0;
           }
           moveSpeed = Math.max(moveSpeed, MovementUtil.getBaseMoveSpeed());
           playerMoveEvent.setSpeed(moveSpeed);
           hopStage++;
        });
        this.onUpdatePositionEvent = (updatePositionEvent -> {
            if (updatePositionEvent.isPre()) {
                if (updatePositionEvent.isOnGround())
                    updatePositionEvent.setY(updatePositionEvent.getY() + 0.015625D);
                final double xDist = Wrapper.getPlayer().posX - Wrapper.getPlayer().prevPosX;
                final double zDist = Wrapper.getPlayer().posZ - Wrapper.getPlayer().prevPosZ;
                lastDist = Math.sqrt(xDist * xDist + zDist * zDist);
                if (hopStage == 2 && MovementUtil.isMoving() && Wrapper.getPlayer().onGround) {
                    Wrapper.getPlayer().jump();
                }
                if (glideProperty.getPropertyValue() && Wrapper.getPlayer().motionY < 0) {
                    Wrapper.getPlayer().motionY += 0.005D;
                }
            }
        });
    }

}
