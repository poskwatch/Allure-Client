package vip.allureclient.impl.module.visual;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.AxisAlignedBB;
import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.annotations.ModuleData;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.base.util.visual.GLUtil;
import vip.allureclient.impl.event.visual.Render3DEvent;

@ModuleData(moduleName = "Chest ESP", moduleBind = 0, moduleCategory = ModuleCategory.VISUAL)
public class ChestESP extends Module {

    @EventListener
    EventConsumer<Render3DEvent> onRender3DEvent;

    public ChestESP() {
        this.onRender3DEvent = (event -> {
            // Draw
            for (int i = 0; i < Wrapper.getWorld().loadedTileEntityList.size(); i++) {
                TileEntity tileEntity = Wrapper.getWorld().loadedTileEntityList.get(i);
                if (tileEntity instanceof TileEntityChest) {
                    TileEntityLockable tileEntityLockable = (TileEntityLockable) tileEntity;
                    GLUtil.glAxisAlignedBBQuad(
                            new AxisAlignedBB(tileEntityLockable.getPos().getX(),
                                    tileEntityLockable.getPos().getY(),
                                    tileEntityLockable.getPos().getZ(),
                                    tileEntityLockable.getPos().getX() + 1,
                                    tileEntityLockable.getPos().getY() + 1,
                                    tileEntityLockable.getPos().getZ() + 1), 0x90ffffff
                    );
                }
            }
        });
    }

}
