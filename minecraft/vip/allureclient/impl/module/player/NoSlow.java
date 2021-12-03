package vip.allureclient.impl.module.player;

import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.ModuleCategory;
import vip.allureclient.base.module.ModuleData;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.base.util.player.MovementUtil;
import vip.allureclient.impl.event.player.UpdatePositionEvent;
import vip.allureclient.impl.module.combat.killaura.KillAura;
import vip.allureclient.impl.property.EnumProperty;

@ModuleData(moduleName = "No Slow", moduleBind = 0, moduleCategory = ModuleCategory.PLAYER)
public class NoSlow extends Module {

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;

    private final EnumProperty<NoSlowModes> noSlowModeProperty = new EnumProperty<>("Mode", NoSlowModes.NCP, this);

    public NoSlow() {
        this.onUpdatePositionEvent = (updatePositionEvent -> {
            setModuleSuffix(noSlowModeProperty.getEnumValueAsString());
            if (noSlowModeProperty.getPropertyValue().equals(NoSlowModes.NCP) && MovementUtil.isMoving() && Wrapper.getPlayer().isBlocking() && !KillAura.getInstance().isBlocking()) {
                if (updatePositionEvent.isPre()) {
                    Wrapper.sendPacketDirect(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                }
                else {
                    Wrapper.sendPacketDirect(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, null, 0.0F, 0.0F, 0.0F));
                }
            }
        });
    }

    private enum NoSlowModes {
        Vanilla,
        NCP
    }

}
