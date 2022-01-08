package vip.allureclient.impl.module.movement;

import org.lwjgl.input.Keyboard;
import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.module.annotations.ModuleData;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.base.util.player.MovementUtil;
import vip.allureclient.impl.event.player.PlayerMoveEvent;
import vip.allureclient.impl.event.player.UpdatePositionEvent;
import vip.allureclient.impl.property.BooleanProperty;
import vip.allureclient.impl.property.ValueProperty;

@ModuleData(moduleName = "Speed", moduleBind = Keyboard.KEY_V, moduleCategory = ModuleCategory.MOVEMENT)
public class Speed extends Module {

    private final BooleanProperty lowHopProperty = new BooleanProperty("Reduce Y-Motion", true, this);

    private final ValueProperty<Float> timerBoostProperty = new ValueProperty<>("Timer Boost", 1.15F, 1.0F, 2.0F,  this);

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;

    @EventListener
    EventConsumer<PlayerMoveEvent> onPlayerMoveEvent;

    private int groundTicks;

    public Speed() {
        this.onUpdatePositionEvent = (event -> {
            setModuleSuffix("Watchdog");
            if (MovementUtil.isMoving() && !Wrapper.getPlayer().isSneaking()) {
                Wrapper.getMinecraft().timer.timerSpeed = timerBoostProperty.getPropertyValue();
               if (Wrapper.getPlayer().onGround) {
                   groundTicks++;
                   if (groundTicks >= 2)
                       Wrapper.getPlayer().jump();
               }
               else
                   groundTicks = 0;
            }
            else
                Wrapper.getMinecraft().timer.timerSpeed = 1.0F;
        });
        this.onPlayerMoveEvent = (event -> {
           if (event.isMoving() && !Wrapper.getPlayer().isSneaking()) {
                event.setSpeed(Wrapper.getPlayer().onGround ? MovementUtil.getBaseMoveSpeed() * 1.9138 : MovementUtil.getBaseMoveSpeed());
                if (lowHopProperty.getPropertyValue() && !MovementUtil.isOverVoid() && event.getY() < 0) {
                    event.setY(event.getY() - 0.15);
                }
           }
        });
    }

    @Override
    public void onEnable() {
        groundTicks = 0;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        Wrapper.getMinecraft().timer.timerSpeed = 1.0F;
        Wrapper.getPlayer().motionX = 0;
        Wrapper.getPlayer().motionZ = 0;
        super.onDisable();
    }
}
