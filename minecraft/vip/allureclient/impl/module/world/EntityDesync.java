package vip.allureclient.impl.module.world;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import vip.allureclient.AllureClient;
import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.module.annotations.ModuleData;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.base.util.visual.ChatUtil;
import vip.allureclient.impl.event.player.UpdatePositionEvent;
import vip.allureclient.visual.notification.NotificationType;

@ModuleData(moduleName = "Entity Desync", moduleBind = 0, moduleCategory = ModuleCategory.WORLD)
public class EntityDesync extends Module {

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;

    public EntityDesync() {
        this.onUpdatePositionEvent = (event -> {
            for (final Entity target : Wrapper.getWorld().loadedEntityList) {
                if (target instanceof EntityBlaze
                        || target instanceof EntitySpider
                        || target instanceof EntityCreeper
                        || target instanceof EntityEnderman
                        || target instanceof EntityZombie
                        || target instanceof EntitySkeleton
                        || target instanceof EntityPig
                        || target instanceof EntitySheep) {
                    if (Wrapper.getPlayer().ticksExisted % 12 == 0 && (Wrapper.getPlayer().getDistanceToEntity(target) < 6)) {
                        Wrapper.getMinecraft().playerController.interactWithEntitySendPacket(Wrapper.getPlayer(), target);
                        AllureClient.getInstance().getNotificationManager().addNotification("Watchdog Disabled",
                                "You may now fly for 4 seconds", 4000, NotificationType.SUCCESS);
                        toggle();
                    }
                }
            }
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
