package vip.allureclient.impl.module.player;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import vip.allureclient.AllureClient;
import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.annotations.ModuleData;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.base.util.player.IRotations;
import vip.allureclient.base.util.player.MovementUtil;
import vip.allureclient.impl.event.network.PacketReceiveEvent;
import vip.allureclient.impl.event.network.PacketSendEvent;
import vip.allureclient.impl.event.player.UpdatePositionEvent;
import vip.allureclient.impl.module.world.Scaffold;

@ModuleData(moduleName = "Disabler", moduleBind = 0, moduleCategory = ModuleCategory.PLAYER)
public class Disabler extends Module implements IRotations {

    @EventListener
    EventConsumer<PacketReceiveEvent> onPacketReceiveEvent;

    @EventListener
    EventConsumer<PacketSendEvent> onPacketSendEvent;

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;

    private int received;
    private boolean C0FState;

    public Disabler() {
        this.onPacketReceiveEvent = (event -> {
            final Packet<?> packet = event.getPacket();
            if (packet instanceof S08PacketPlayerPosLook) {
                S08PacketPlayerPosLook S08 = (S08PacketPlayerPosLook) packet;
                double x = S08.getX();
                double y = S08.getY();
                double z = S08.getZ();
                if (Minecraft.getMinecraft().thePlayer != null && !event.isCancelled() && Minecraft.getMinecraft().thePlayer.ticksExisted < 80) {
                    received++;
                    if (received <= 3) {
                        PlayerCapabilities capabilities = new PlayerCapabilities();
                        capabilities.allowFlying = true;
                        capabilities.isFlying = true;
                        Wrapper.sendPacketDirect(new C13PacketPlayerAbilities(capabilities));
                    }
                    event.setCancelled(true);
                    Wrapper.getPlayer().closeScreen();
                    Wrapper.getPlayer().setPosition(x, y, z);
                }
            }
        });
        this.onPacketSendEvent = (event -> {
            final Packet<?> packet = event.getPacket();
            if (packet instanceof C0FPacketConfirmTransaction) {
                C0FState = true;
            }
            if (packet instanceof C03PacketPlayer) {
                C03PacketPlayer c03 = (C03PacketPlayer) packet;
                if (!c03.isMoving() && !c03.getRotating())
                    event.setCancelled(true);
            }
        });
        this.onUpdatePositionEvent = (event -> {
            if (event.isPre() && Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().thePlayer.ticksExisted % (C0FState ? 50 : 30) == 0) {
                if (!C0FState) {
                    Wrapper.sendPacketDirect(new C00PacketKeepAlive(18));
                } else {
                    Wrapper.sendPacketDirect(new C00PacketKeepAlive(Wrapper.getPlayer().ticksExisted));
                }
            }
            if (MovementUtil.isMoving() && !AllureClient.getInstance().getModuleManager().getModuleByClass.apply(Scaffold.class).isToggled()) {
                setRotations(event, getRotations(), false);
            }
        });
    }

    @Override
    public float[] getRotations() {
        return new float[]{MovementUtil.getMovementDirection(), Wrapper.getPlayer().rotationPitch};
    }

    @Override
    public void setRotations(UpdatePositionEvent event, float[] rotations, boolean visualize) {
        event.setYaw(rotations[0], visualize);
        event.setPitch(rotations[1], visualize);
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
