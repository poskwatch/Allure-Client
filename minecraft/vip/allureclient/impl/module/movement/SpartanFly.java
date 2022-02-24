package vip.allureclient.impl.module.movement;

import io.github.poskwatch.eventbus.api.annotations.EventHandler;
import io.github.poskwatch.eventbus.api.interfaces.IEventCallable;
import io.github.poskwatch.eventbus.api.interfaces.IEventListener;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.util.player.MovementUtil;
import vip.allureclient.impl.event.events.player.PlayerMoveEvent;
import vip.allureclient.impl.event.events.player.UpdatePositionEvent;

public class SpartanFly extends Module {

    public SpartanFly() {
        super("Spartan Fly", ModuleCategory.MOVEMENT);
        this.setListener(new IEventListener() {
            @EventHandler(events = UpdatePositionEvent.class)
            final IEventCallable<UpdatePositionEvent> onUpdatePosition = event -> {
                event.setOnGround(true);
                mc.thePlayer.onGround = true;
                mc.thePlayer.motionY = 0;
            };
            @EventHandler(events = PlayerMoveEvent.class)
            final IEventCallable<PlayerMoveEvent> onPlayerMove = event -> {
                event.setY(0);
                event.setSpeed(MovementUtil.getBaseMoveSpeed());
            };
        });
    }

}
