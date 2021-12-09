package vip.allureclient.impl.module.movement;

import net.minecraft.network.play.client.C03PacketPlayer;
import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.ModuleCategory;
import vip.allureclient.base.module.ModuleData;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.impl.event.network.PacketSendEvent;
import vip.allureclient.impl.event.player.PlayerStepEvent;
import vip.allureclient.impl.event.player.UpdatePositionEvent;
import vip.allureclient.impl.property.BooleanProperty;

@ModuleData(moduleName = "Step", moduleBind = 0, moduleCategory = ModuleCategory.MOVEMENT)
public class Step extends Module {

    @EventListener
    EventConsumer<PlayerStepEvent> onPlayerStepEvent;

    @EventListener
    EventConsumer<PacketSendEvent> onPacketSendEvent;

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;

    private final BooleanProperty cancelExtraPackets = new BooleanProperty("Less Packets", true, this);

    private int cancelledPackets;
    private boolean cancelThePackets;

    private final double[] stepOffsets = new double[] { 0.41999998688697815D, 0.7531999945640564D };

    public Step() {
        onPlayerStepEvent = (playerStepEvent -> {
           if (playerStepEvent.isPre() && Wrapper.getPlayer().onGround) {
               playerStepEvent.setStepHeight(1.0F);
           }
           else {
               for (double stepOffset : stepOffsets) {
                   Wrapper.sendPacketDirect(new C03PacketPlayer.C04PacketPlayerPosition((Wrapper.getPlayer()).posX, (Wrapper.getPlayer()).posY + stepOffset * playerStepEvent.getHeightStepped(), (Wrapper.getPlayer()).posZ, false));
               }
               cancelThePackets = true;
           }
        });
        onPacketSendEvent = (packetSendEvent -> {
            if (cancelExtraPackets.getPropertyValue() && packetSendEvent.getPacket() instanceof C03PacketPlayer) {
                if (cancelledPackets > 0) {
                    cancelThePackets = false;
                    cancelledPackets = 0;
                    Wrapper.getMinecraft().timer.timerSpeed = 1.0F;
                }
                if (cancelThePackets) {
                    Wrapper.getMinecraft().timer.timerSpeed = 0.3F;
                    this.cancelledPackets = cancelledPackets + 1;
                }
            }
        });
        onUpdatePositionEvent = (updatePositionEvent -> setModuleSuffix(cancelExtraPackets.getPropertyValue() ? "NCP" : "Vanilla"));
    }
}
