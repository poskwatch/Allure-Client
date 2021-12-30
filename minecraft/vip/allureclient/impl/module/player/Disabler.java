package vip.allureclient.impl.module.player;

import net.minecraft.network.play.client.C03PacketPlayer;
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
import vip.allureclient.impl.event.player.UpdatePositionEvent;
import vip.allureclient.impl.property.BooleanProperty;

@ModuleData(moduleName = "Disabler", moduleBind = 0, moduleCategory = ModuleCategory.PLAYER)
public class Disabler extends Module implements IRotations {

    private final BooleanProperty antiStaffFlagProperty = new BooleanProperty("Anti-Staff Flag", true, this);

    @EventListener
    EventConsumer<PacketReceiveEvent> onPacketReceiveEvent;

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;

    public Disabler() {
        this.onPacketReceiveEvent = (event -> {
            if (event.getPacket() instanceof S08PacketPlayerPosLook) {
                if (Wrapper.getPlayer().ticksExisted < 100 && !Wrapper.isSinglePlayer()) {
                    event.setCancelled(true);
                    if (antiStaffFlagProperty.getPropertyValue()) {
                        S08PacketPlayerPosLook s08 = (S08PacketPlayerPosLook) event.getPacket();
                        Wrapper.sendPacketDirect(new C03PacketPlayer.C06PacketPlayerPosLook(
                                s08.getX(), s08.getY(), s08.getZ(),
                                s08.getYaw(), s08.getPitch(),
                                Wrapper.getPlayer().onGround));
                    }
                }
            }
        });
        this.onUpdatePositionEvent = (event -> setRotations(event, getRotations(), false));
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
