package vip.allureclient.impl.module.player;

import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.module.annotations.ModuleData;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.base.util.visual.ChatUtil;
import vip.allureclient.impl.event.network.PacketReceiveEvent;
import vip.allureclient.impl.event.player.UpdatePositionEvent;

@ModuleData(moduleName = "Disabler", moduleBind = 0, moduleCategory = ModuleCategory.PLAYER)
public class Disabler extends Module {

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;

    @EventListener
    EventConsumer<PacketReceiveEvent> onPacketReceiveEvent;

    public Disabler() {
        onUpdatePositionEvent = (event -> {
            if (event.isPre()) {
                Wrapper.sendPacketDirect(new C08PacketPlayerBlockPlacement(null));
            }
        });
        onPacketReceiveEvent = (event -> {
            if (event.getPacket() instanceof S08PacketPlayerPosLook && Wrapper.getPlayer().ticksExisted > 50) {
                S08PacketPlayerPosLook s08 = (S08PacketPlayerPosLook) event.getPacket();
                final double x = s08.getX();
                final double y = s08.getY();
                final double z = s08.getZ();
                if (Wrapper.getPlayer().getDistance(x, y, z) < 8) {
                    Wrapper.sendPacketDirect(new C03PacketPlayer.C06PacketPlayerPosLook(x, y, z, s08.getYaw(), s08.getPitch(), false));
                    event.setCancelled(true);
                    ChatUtil.sendMessageToPlayer("§4§lFlag Packet Blocked");
                }
            }
        });
    }
}
