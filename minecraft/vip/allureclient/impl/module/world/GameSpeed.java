package vip.allureclient.impl.module.world;

import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.module.annotations.ModuleData;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.impl.event.network.PacketSendEvent;
import vip.allureclient.impl.event.player.UpdatePositionEvent;
import vip.allureclient.impl.property.BooleanProperty;
import vip.allureclient.impl.property.ValueProperty;

@ModuleData(moduleName = "Game Speed", moduleBind = 0, moduleCategory = ModuleCategory.WORLD)
public class GameSpeed extends Module {

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;

    @EventListener
    EventConsumer<PacketSendEvent> onPacketSendEvent;

    private final ValueProperty<Float> timerSpeedProperty = new ValueProperty<>("Speed", 2.0F, 0.1F, 10.0F, this);

    private final BooleanProperty hypixelBypassProperty = new BooleanProperty("Hypixel", true, this);

    public GameSpeed () {
        onUpdatePositionEvent = (updatePositionEvent -> Wrapper.getMinecraft().timer.timerSpeed = timerSpeedProperty.getPropertyValue());
        onPacketSendEvent = (packetSendEvent -> {
           if (packetSendEvent.getPacket() instanceof C0FPacketConfirmTransaction && hypixelBypassProperty.getPropertyValue())
               packetSendEvent.setCancelled(true);
        });
    }

    @Override
    public void onDisable() {
        Wrapper.getMinecraft().timer.timerSpeed = 1.0F;
    }
}
