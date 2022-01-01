package vip.allureclient.impl.module.player;

import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.module.annotations.ModuleData;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.base.util.player.IRotations;
import vip.allureclient.base.util.player.MovementUtil;
import vip.allureclient.impl.event.network.PacketReceiveEvent;
import vip.allureclient.impl.event.network.PacketSendEvent;
import vip.allureclient.impl.event.player.UpdatePositionEvent;
import vip.allureclient.impl.event.world.WorldLoadEvent;

@ModuleData(moduleName = "Disabler", moduleBind = 0, moduleCategory = ModuleCategory.PLAYER)
public class Disabler extends Module implements IRotations {

    @EventListener
    EventConsumer<PacketReceiveEvent> onPacketReceiveEvent;

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;

    @EventListener
    EventConsumer<PacketSendEvent> onPacketSendEvent;

    @EventListener
    EventConsumer<WorldLoadEvent> onWorldLoadEvent;

    private int received;
    private boolean C0FState;

    public Disabler() {
        this.onPacketReceiveEvent = (event -> {
            if (event.getPacket() instanceof S08PacketPlayerPosLook) {
                S08PacketPlayerPosLook s08 = (S08PacketPlayerPosLook) event.getPacket();
                if (Wrapper.getPlayer().ticksExisted < 80 && !event.isCancelled() && Wrapper.getPlayer() != null) {
                    received++;
                    event.setCancelled(true);

                    if (received <= 3) {
                        PlayerCapabilities capabilities = new PlayerCapabilities();
                        capabilities.allowFlying = true;
                        capabilities.isFlying = true;
                        Wrapper.sendPacketDirect(new C13PacketPlayerAbilities(capabilities));
                    }

                    Wrapper.getMinecraft().displayGuiScreen(null);
                    Wrapper.getPlayer().setPosition(s08.getX(), s08.getY(), s08.getZ());
                }
            }
        });
        this.onPacketSendEvent = (event -> {
           if (event.getPacket() instanceof C0FPacketConfirmTransaction)
               C0FState = true;
        });
        this.onWorldLoadEvent = (event -> {
            C0FState = false;
            received = 0;
        });
        this.onUpdatePositionEvent = (event -> {
            setModuleSuffix("Watchdog");
            setRotations(event, getRotations(), false);
            if (event.isPre() && Wrapper.getPlayer().ticksExisted % (C0FState ? 50 : 30) == 0) {
                if (!C0FState) {
                    Wrapper.sendPacketDirect(new C00PacketKeepAlive(18));
                } else {
                    Wrapper.sendPacketDirect(new C00PacketKeepAlive(Wrapper.getPlayer().ticksExisted));
                }
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
