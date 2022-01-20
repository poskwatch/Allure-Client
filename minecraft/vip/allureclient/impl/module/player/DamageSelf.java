package vip.allureclient.impl.module.player;

import net.minecraft.network.play.server.S12PacketEntityVelocity;
import vip.allureclient.AllureClient;
import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.annotations.ModuleData;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.util.client.TimerUtil;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.impl.event.network.PacketReceiveEvent;
import vip.allureclient.impl.event.player.UpdatePositionEvent;
import vip.allureclient.visual.notification.NotificationType;

@ModuleData(moduleName = "Damage Self", moduleBind = 0, moduleCategory = ModuleCategory.PLAYER)
public class DamageSelf extends Module {

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;

    private final TimerUtil timerUtil = new TimerUtil();

    public DamageSelf() {
        this.onUpdatePositionEvent = (event -> {
            event.setY(event.getY() + 3);
            event.setOnGround(false);
            event.setOnGround(true);

            if (timerUtil.hasReached(300))
                setToggled(false);
        });
    }

    @Override
    public void onEnable() {
        timerUtil.reset();
        Wrapper.getPlayer().jump();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
