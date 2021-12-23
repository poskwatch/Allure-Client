package vip.allureclient.impl.module.movement;

import org.lwjgl.input.Keyboard;
import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.module.annotations.ModuleData;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.base.util.player.MovementUtil;
import vip.allureclient.impl.event.player.UpdatePositionEvent;

@ModuleData(moduleName = "Speed", moduleBind = Keyboard.KEY_V, moduleCategory = ModuleCategory.MOVEMENT)
public class Speed extends Module {

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;

    public Speed() {
        this.onUpdatePositionEvent = (event -> {
            event.setYaw(MovementUtil.getMovementDirection());
           if (MovementUtil.isMoving()) {
               MovementUtil.setSpeed(0.3);
               if (Wrapper.getPlayer().onGround)
                Wrapper.getPlayer().jump();
           }
           else {
               MovementUtil.setSpeed(0);
           }
        });
    }

}
