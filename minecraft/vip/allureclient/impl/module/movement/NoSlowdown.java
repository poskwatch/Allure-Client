package vip.allureclient.impl.module.movement;

import io.github.poskwatch.eventbus.api.annotations.EventHandler;
import io.github.poskwatch.eventbus.api.interfaces.IEventCallable;
import io.github.poskwatch.eventbus.api.interfaces.IEventListener;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import vip.allureclient.AllureClient;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.util.player.MovementUtil;
import vip.allureclient.base.util.player.PacketUtil;
import vip.allureclient.impl.event.events.player.UpdatePositionEvent;
import vip.allureclient.impl.module.combat.KillAura;
import vip.allureclient.impl.property.EnumProperty;

public class NoSlowdown extends Module {

    private final EnumProperty<NoSlowMode> noSlowModeProperty = new EnumProperty<>("Mode", NoSlowMode.NCP, this);

    public NoSlowdown() {
        super("No Slowdown", ModuleCategory.MOVEMENT);
        this.setListener(new IEventListener() {
            @EventHandler(events = UpdatePositionEvent.class)
            final IEventCallable<UpdatePositionEvent> onUpdatePosition = (event -> {
                if (MovementUtil.isMoving() && mc.thePlayer.isBlocking() && !KillAura.getInstance().isBlocking()) {
                    if (noSlowModeProperty.getPropertyValue().equals(NoSlowMode.NCP)) {
                        if (event.isPre())
                            // Before update packets are sent, release block
                            PacketUtil.sendPacketDirect(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                        else
                            // After packets are sent, placement packet blocks sword
                            PacketUtil.sendPacketDirect(new C08PacketPlayerBlockPlacement(
                                    new BlockPos(-1, -1, -1), 255, null, 0.0F, 0.0F, 0.0F));
                    }
                }
            });
        });
        noSlowModeProperty.onValueChange = () -> setModuleSuffix(noSlowModeProperty.getPropertyValue().toString());
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public static NoSlowdown getInstance() {
        return (NoSlowdown) AllureClient.getInstance().getModuleManager().getModuleOrNull("No Slowdown");
    }

    private enum NoSlowMode {
        VANILLA("Vanilla"),
        NCP("NCP");

        private final String name;

        NoSlowMode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

}
