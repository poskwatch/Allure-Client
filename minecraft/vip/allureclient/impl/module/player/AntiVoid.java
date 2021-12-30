package vip.allureclient.impl.module.player;

import net.minecraft.network.play.client.C03PacketPlayer;
import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.module.annotations.ModuleData;
import vip.allureclient.base.util.client.TimerUtil;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.base.util.player.MovementUtil;
import vip.allureclient.impl.event.player.UpdatePositionEvent;
import vip.allureclient.impl.property.EnumProperty;
import vip.allureclient.impl.property.ValueProperty;

@ModuleData(moduleName = "Anti Void", moduleBind = 0, moduleCategory = ModuleCategory.PLAYER)
public class AntiVoid extends Module {

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePosition;

    private final ValueProperty<Double> distanceProperty = new ValueProperty<>("Distance", 5.0D, 3.0D, 10.0D, this);

    private final EnumProperty<AntiFallMode> antiFallModeProperty = new EnumProperty<>("Mode", AntiFallMode.Packet, this);

    private final TimerUtil ticksSincePullTimer = new TimerUtil();

    public AntiVoid() {
        onUpdatePosition = (event -> {
            if (event.isPre()) {
                if (Wrapper.getPlayer().fallDistance >= distanceProperty.getPropertyValue() && !Wrapper.getPlayer().onGround && ticksSincePullTimer.hasReached(500) && MovementUtil.isOverVoid()) {
                    if (antiFallModeProperty.getPropertyValue().equals(AntiFallMode.Packet)) {
                        Wrapper.sendPacketDirect(new C03PacketPlayer.C06PacketPlayerPosLook(event.getX(),
                                event.getY() + 9.0D + StrictMath.random(), event.getZ(), event.getYaw(), event.getPitch(), false));
                    }
                    else {
                        if (Wrapper.getPlayer().motionY < 0) {
                            Wrapper.getPlayer().motionY = 2.22d;
                        }
                    }
                    Wrapper.getPlayer().fallDistance = 0;
                    ticksSincePullTimer.reset();
                }
            }
        });
        distanceProperty.onValueChange = () -> setModuleSuffix(antiFallModeProperty.getEnumValueAsString());
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    private enum AntiFallMode {
        Packet,
        Motion
    }
}
