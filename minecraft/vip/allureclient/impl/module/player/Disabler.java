package vip.allureclient.impl.module.player;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.ModuleCategory;
import vip.allureclient.base.module.ModuleData;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.base.util.visual.ChatUtil;
import vip.allureclient.impl.event.network.PacketReceiveEvent;
import vip.allureclient.impl.event.network.PacketSendEvent;
import vip.allureclient.impl.event.world.WorldLoadEvent;
import vip.allureclient.impl.property.EnumProperty;

@ModuleData(moduleName = "Disabler", moduleBind = 0, moduleCategory = ModuleCategory.PLAYER)
public class Disabler extends Module {

    private long timeOfFlag;

    @EventListener
    EventConsumer<PacketSendEvent> onPacketSendEvent;

    @EventListener
    EventConsumer<PacketReceiveEvent> onPacketReceiveEvent;

    @EventListener
    EventConsumer<WorldLoadEvent> onWorldLoadEvent;

    private final EnumProperty<DisablerMode> disablerModeProperty = new EnumProperty<>("Mode", DisablerMode.Watchdog, this);

    public Disabler() {
        this.onPacketSendEvent = (packetSendEvent -> {
            final Packet<?> packet = packetSendEvent.getPacket();
            if (packet instanceof C03PacketPlayer.C06PacketPlayerPosLook && System.currentTimeMillis() - timeOfFlag < 25 + (Math.random() * 25) && Wrapper.getPlayer().ticksExisted > 1) {
                final C03PacketPlayer.C06PacketPlayerPosLook c06 = (C03PacketPlayer.C06PacketPlayerPosLook) packet;
                packetSendEvent.setPacket(new C03PacketPlayer.C04PacketPlayerPosition(c06.getPositionX(), c06.getPositionY(), c06.getPositionZ(), c06.isOnGround()));
            }
        });
        this.onPacketReceiveEvent = (packetReceiveEvent -> {
            final Packet<?> packet = packetReceiveEvent.getPacket();
            if (packet instanceof S08PacketPlayerPosLook) {
                timeOfFlag = System.currentTimeMillis();
                ChatUtil.sendMessageToPlayer("Disabler active.");
            }
            if (packet instanceof S08PacketPlayerPosLook) {
                ((S08PacketPlayerPosLook) packet).yaw = Wrapper.getPlayer().rotationYaw;
                ((S08PacketPlayerPosLook) packet).pitch = Wrapper.getPlayer().rotationPitch;
                Wrapper.sendPacketDirect(new C03PacketPlayer.C05PacketPlayerLook(Wrapper.getPlayer().rotationYaw, Wrapper.getPlayer().rotationPitch, Wrapper.getPlayer().onGround));
            }
        });
        this.onWorldLoadEvent = (worldLoadEvent -> {
            timeOfFlag = 0;
            ChatUtil.sendMessageToPlayer("Disabler active.");
        });
        this.onModuleEnabled = () -> {
            timeOfFlag = 0;
            ChatUtil.sendMessageToPlayer("Disabler active.");
        };
        disablerModeProperty.onValueChange = () -> setModuleSuffix(disablerModeProperty.getEnumValueAsString());
    }

    private enum DisablerMode {
        Watchdog,
        BlocksMC
    }

}
