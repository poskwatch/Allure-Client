package vip.allureclient.impl.module.player;

import io.github.poskwatch.eventbus.api.annotations.EventHandler;
import io.github.poskwatch.eventbus.api.interfaces.IEventCallable;
import io.github.poskwatch.eventbus.api.interfaces.IEventListener;
import net.minecraft.network.play.client.C03PacketPlayer;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.util.player.PacketUtil;
import vip.allureclient.base.util.visual.ChatUtil;
import vip.allureclient.impl.event.events.player.UpdatePositionEvent;

public class Regeneration extends Module {

    public Regeneration() {
        super("Regeneration", ModuleCategory.PLAYER);
        this.setListener(new IEventListener() {
            @EventHandler(events = UpdatePositionEvent.class)
            final IEventCallable<UpdatePositionEvent> onUpdatePosition = (event -> {
               if (event.isPre() && event.isOnGround()) {
                   for (int i = 0; i < 8; i++) {
                       PacketUtil.sendPacketDirect(new C03PacketPlayer(true));
                       ChatUtil.sendMessageToPlayer("Regenerated.");
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
