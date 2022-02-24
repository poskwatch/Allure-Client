package vip.allureclient.impl.module.world;

import io.github.poskwatch.eventbus.api.annotations.EventHandler;
import io.github.poskwatch.eventbus.api.interfaces.IEventCallable;
import io.github.poskwatch.eventbus.api.interfaces.IEventListener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import vip.allureclient.AllureClient;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.impl.event.events.player.UpdatePositionEvent;
import vip.allureclient.visual.notification.NotificationType;

public class EntityDesync extends Module {

    public EntityDesync() {
        super("Entity Desync", ModuleCategory.WORLD);
        this.setListener(new IEventListener() {
            @EventHandler(events = UpdatePositionEvent.class)
            final IEventCallable<UpdatePositionEvent> onUpdatePosition = (event -> {
                for (final Entity target : mc.theWorld.loadedEntityList) {
                    if (target instanceof EntityBlaze
                            || target instanceof EntitySpider
                            || target instanceof EntityCreeper
                            || target instanceof EntityEnderman
                            || target instanceof EntityZombie
                            || target instanceof EntitySkeleton
                            || target instanceof EntityPig
                            || target instanceof EntitySheep) {
                        if (mc.thePlayer.ticksExisted % 12 == 0 && (mc.thePlayer.getDistanceToEntity(target) < 6)) {
                            mc.playerController.interactWithEntitySendPacket(mc.thePlayer, target);
                            AllureClient.getInstance().getNotificationManager().addNotification("Watchdog Disabled",
                                    "You may now fly for 4 seconds", 4000, NotificationType.SUCCESS);
                            setToggled(false);
                        }
                    }
                }
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
