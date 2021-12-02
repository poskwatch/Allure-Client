package vip.allureclient.impl.module.movement;

import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.ModuleCategory;
import vip.allureclient.base.module.ModuleData;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.impl.event.player.PlayerMoveEvent;

@ModuleData(moduleName = "Sprint", moduleBind = 0, moduleCategory = ModuleCategory.MOVEMENT)
public class Sprint extends Module {

    @EventListener
    EventConsumer<PlayerMoveEvent> onPlayerMoveEvent;

    public Sprint(){
        this.onPlayerMoveEvent = (moveEvent -> {
           if (Wrapper.getPlayer().moveForward > 0 && (Wrapper.getPlayer().capabilities.isCreativeMode || Wrapper.getPlayer().getFoodStats().getFoodLevel() > 3)){
               Wrapper.getPlayer().setSprinting(true);
           }
        });
    }
}
