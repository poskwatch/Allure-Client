package vip.allureclient.impl.module.combat;

import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.ModuleCategory;
import vip.allureclient.base.module.ModuleData;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.impl.event.player.UpdatePositionEvent;

@ModuleData(moduleName = "Anti Bot", keyBind = 0, category = ModuleCategory.COMBAT)
public class AntiBot extends Module {

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;

    public AntiBot() {
        onUpdatePositionEvent = (updatePositionEvent -> Wrapper.getWorld().loadedEntityList.forEach(entity -> {
            if(entity.isInvisible() && entity != Wrapper.getPlayer())
                Wrapper.getWorld().removeEntity(entity);
        }));
    }

}

