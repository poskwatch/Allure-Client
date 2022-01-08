package vip.allureclient.impl.module.player;

import net.minecraft.network.play.server.S12PacketEntityVelocity;
import vip.allureclient.AllureClient;
import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.annotations.ModuleData;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.impl.event.network.PacketReceiveEvent;
import vip.allureclient.impl.event.player.UpdatePositionEvent;
import vip.allureclient.visual.notification.NotificationType;

@ModuleData(moduleName = "Damage Self", moduleBind = 0, moduleCategory = ModuleCategory.PLAYER)
public class DamageSelf extends Module {

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;

    @EventListener
    EventConsumer<PacketReceiveEvent> onPacketReceiveEvent;

    public DamageSelf() {
        this.onPacketReceiveEvent = (packetReceiveEvent -> {
           if (packetReceiveEvent.getPacket() instanceof S12PacketEntityVelocity) {
               if (((S12PacketEntityVelocity) packetReceiveEvent.getPacket()).getEntityID() == Wrapper.getPlayer().getEntityId()) {
                   AllureClient.getInstance().getNotificationManager().addNotification("Damaged",
                           "You have been damaged", 1500, NotificationType.WARNING);
                   toggle();
               }
           }
        });
        this.onUpdatePositionEvent = (event -> {

        });
    }
}
