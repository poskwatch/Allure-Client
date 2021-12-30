package vip.allureclient.impl.module.movement;

import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.annotations.ModuleData;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.base.util.player.MovementUtil;
import vip.allureclient.impl.event.player.PlayerMoveEvent;
import vip.allureclient.impl.event.player.UpdatePositionEvent;

@ModuleData(moduleName = "Verus Flight", moduleBind = 0, moduleCategory = ModuleCategory.MOVEMENT)
public class VerusFlight extends Module {

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;

    @EventListener
    EventConsumer<PlayerMoveEvent> onPlayerMoveEvent;

    public VerusFlight() {
        this.onUpdatePositionEvent = (event -> {
            if (Wrapper.getPlayer().ticksExisted % 2 == 0)
                Wrapper.getPlayer().motionY += 0.1;
            Wrapper.getMinecraft().timer.timerSpeed = 0.4f;
        });
        this.onPlayerMoveEvent = (event -> {
            event.setSpeed(3);
        });
    }

    @Override
    public void onEnable() {
        MovementUtil.damagePlayer();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        Wrapper.getMinecraft().timer.timerSpeed = 1.0f;
        super.onDisable();
    }
}
