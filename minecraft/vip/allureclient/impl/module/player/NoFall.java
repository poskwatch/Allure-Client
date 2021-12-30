package vip.allureclient.impl.module.player;

import net.minecraft.network.play.client.C03PacketPlayer;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.module.annotations.ModuleData;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.impl.event.player.UpdatePositionEvent;
import vip.allureclient.impl.property.EnumProperty;

@ModuleData(moduleName = "No Fall", moduleBind = 0, moduleCategory = ModuleCategory.PLAYER)
public class NoFall extends Module {

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;

    private final EnumProperty<NoFallModes> mode = new EnumProperty<>("Mode", NoFallModes.Edit, this);

    private enum NoFallModes {
        Packet,
        Edit
    }

    public NoFall() {
        this.onUpdatePositionEvent = (updatePositionEvent -> {
            setModuleSuffix(mode.getEnumValueAsString());
            if(updatePositionEvent.isPre()) {
                if (mode.getPropertyValue().equals(NoFallModes.Packet)) {
                    if (Wrapper.getPlayer().fallDistance >= 3) {
                        Wrapper.getPlayer().sendQueue.addToSendQueue(new C03PacketPlayer(true));
                    }
                }
                if (mode.getPropertyValue().equals(NoFallModes.Edit)) {
                    if (Wrapper.getPlayer().fallDistance >= 3) {
                        updatePositionEvent.setOnGround(true);
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
