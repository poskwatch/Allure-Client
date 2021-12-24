package vip.allureclient.impl.module.movement;

import org.lwjgl.input.Keyboard;
import vip.allureclient.AllureClient;
import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.module.annotations.ModuleData;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.base.util.player.MovementUtil;
import vip.allureclient.impl.event.player.PlayerMoveEvent;
import vip.allureclient.impl.event.player.UpdatePositionEvent;
import vip.allureclient.impl.module.visual.HUD;
import vip.allureclient.impl.property.BooleanProperty;
import vip.allureclient.impl.property.EnumProperty;
import vip.allureclient.impl.property.ValueProperty;

@ModuleData(moduleName = "Speed", moduleBind = Keyboard.KEY_V, moduleCategory = ModuleCategory.MOVEMENT)
public class Speed extends Module {

    private final BooleanProperty lowHopProperty = new BooleanProperty("Reduce Y-Motion", true, this);
    private final EnumProperty<LowHopType> lowHopTypeProperty = new EnumProperty<LowHopType>("Reduce Type", LowHopType.Partial, this) {
        @Override
        public boolean isPropertyHidden() {
            return !lowHopProperty.getPropertyValue();
        }
    };
    private final ValueProperty<Float> timerBoostProperty = new ValueProperty<>("Timer Boost", 1.15F, 1.0F, 2.0F,  this);

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;

    @EventListener
    EventConsumer<PlayerMoveEvent> onPlayerMoveEvent;

    private int groundTicks;

    public Speed() {
        this.onUpdatePositionEvent = (event -> {
            event.setYaw(MovementUtil.getMovementDirection());
            if (MovementUtil.isMoving()) {
                Wrapper.getMinecraft().timer.timerSpeed = timerBoostProperty.getPropertyValue();
               if (Wrapper.getPlayer().onGround) {
                   groundTicks++;
                   if (groundTicks >= 3)
                       Wrapper.getPlayer().jump();
               }
               else
                   groundTicks = 0;
            }
            else
                Wrapper.getMinecraft().timer.timerSpeed = 1.0F;
        });
        this.onPlayerMoveEvent = (event -> {
           if (event.isMoving()) {
                event.setSpeed(Wrapper.getPlayer().onGround ? MovementUtil.getBaseMoveSpeed() * 1.2 : MovementUtil.getBaseMoveSpeed() * 1.06);
                if (lowHopProperty.getPropertyValue()) {
                    if (!MovementUtil.isOverVoid() && event.getY() < 0)
                        event.setY(event.getY() - 0.15);
                    if (lowHopTypeProperty.getPropertyValue().equals(LowHopType.Full) && !Wrapper.getPlayer().isCollided) {
                        if (!MovementUtil.isOverVoid() && event.getY() > 0)
                            event.setY(event.getY() - 0.06);
                    }
                }
           }
        });
    }

    @Override
    public void onEnable() {

        groundTicks = 0;
    }

    @Override
    public void onDisable() {
        Wrapper.getMinecraft().timer.timerSpeed = 1.0F;
    }

    private enum LowHopType {
        Partial,
        Full
    }
}
