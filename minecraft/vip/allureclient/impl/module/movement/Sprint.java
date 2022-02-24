package vip.allureclient.impl.module.movement;

import io.github.poskwatch.eventbus.api.annotations.EventHandler;
import io.github.poskwatch.eventbus.api.enums.Priority;
import io.github.poskwatch.eventbus.api.interfaces.IEventCallable;
import io.github.poskwatch.eventbus.api.interfaces.IEventListener;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.impl.event.events.player.PlayerMoveEvent;

public class Sprint extends Module {

    public Sprint(){
        super("Sprint", ModuleCategory.MOVEMENT);
        this.setListener(new IEventListener() {
            @EventHandler(events = PlayerMoveEvent.class, priority = Priority.HIGH)
            final IEventCallable<PlayerMoveEvent> onPlayerMove = (event -> {
                if (mc.thePlayer.moveForward > 0 && (mc.thePlayer.capabilities.isCreativeMode || mc.thePlayer.getFoodStats().getFoodLevel() > 3))
                    mc.thePlayer.setSprinting(true);
            });
        });
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
