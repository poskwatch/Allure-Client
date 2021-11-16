package vip.allureclient.impl.module.movement;

import net.minecraft.client.Minecraft;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.ModuleCategory;
import vip.allureclient.base.module.ModuleData;
import vip.allureclient.base.util.client.AccountUtil;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.base.util.visual.ChatUtil;
import vip.allureclient.impl.event.player.PlayerMoveEvent;

@ModuleData(moduleName = "Sprint", keyBind = 0, category = ModuleCategory.MOVEMENT)
public class Sprint extends Module {

    @EventListener
    EventConsumer<PlayerMoveEvent> onPlayerMoveEvent;

    public Sprint(){
        this.onPlayerMoveEvent = (moveEvent -> {
           if (Wrapper.getPlayer().moveForward > 0 && Wrapper.getPlayer().getFoodStats().getFoodLevel() > 3){
               Wrapper.getPlayer().setSprinting(true);
           }
        });
    }
}
