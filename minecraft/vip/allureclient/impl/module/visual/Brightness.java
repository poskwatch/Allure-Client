package vip.allureclient.impl.module.visual;

import io.github.poskwatch.eventbus.api.annotations.EventHandler;
import io.github.poskwatch.eventbus.api.interfaces.IEventCallable;
import io.github.poskwatch.eventbus.api.interfaces.IEventListener;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.impl.event.events.player.UpdatePositionEvent;

public class Brightness extends Module {

    public Brightness() {
        super("Brightness", ModuleCategory.VISUAL);
        this.setListener(new IEventListener() {
            @EventHandler(events = UpdatePositionEvent.class)
            final IEventCallable<UpdatePositionEvent> onUpdatePosition = event -> {
                if (!mc.thePlayer.isPotionActive(Potion.nightVision))
                    mc.thePlayer.addPotionEffect(new PotionEffect(Potion.nightVision.id, 5200, 68));
            };
        });
    }

}
