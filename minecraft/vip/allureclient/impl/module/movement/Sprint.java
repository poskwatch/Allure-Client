package vip.allureclient.impl.module.movement;

import org.lwjgl.input.Keyboard;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.event.Listener;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.ModuleCategory;
import vip.allureclient.base.module.ModuleData;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.impl.event.player.PlayerMoveEvent;

@ModuleData(label = "Sprint", keyBind = Keyboard.KEY_V, category = ModuleCategory.MOVEMENT)
public class Sprint extends Module {

    @EventListener
    Listener<PlayerMoveEvent> onPlayerMoveEvent;

    public Sprint(){
        this.onPlayerMoveEvent = (moveEvent -> {
           if (Wrapper.getPlayer().moveForward > 0){
               Wrapper.getPlayer().setSprinting(true);
           }
        });
    }
}
