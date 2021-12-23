package vip.allureclient.impl.module.world;

import net.minecraft.network.play.client.C01PacketChatMessage;
import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.module.annotations.ModuleData;
import vip.allureclient.base.util.client.NetworkUtil;
import vip.allureclient.base.util.client.TimerUtil;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.impl.event.player.UpdatePositionEvent;

@ModuleData(moduleName = "Anti Death", moduleBind = 0, moduleCategory = ModuleCategory.WORLD)
public class AntiDeath extends Module {

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;

    private final TimerUtil spamTimer = new TimerUtil();

    public AntiDeath() {
        onUpdatePositionEvent = (updatePositionEvent -> {
           if (spamTimer.hasReached(1000) && Wrapper.getPlayer().getHealth() < 20 && NetworkUtil.isOnServer("eu.loyisa.cn")) {
               Wrapper.sendPacketDirect(new C01PacketChatMessage("/sethealth 20"));
               spamTimer.reset();
           }
        });
    }

    @Override
    public void onEnable() {
        spamTimer.reset();
    }
}
