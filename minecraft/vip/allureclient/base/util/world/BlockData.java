package vip.allureclient.base.util.world;

import net.minecraft.client.Minecraft;
import net.minecraft.util.*;

public class BlockData {
    public final BlockPos pos;
    public final EnumFacing face;
    public final Vec3 hitVec;

    public BlockData(BlockPos pos, EnumFacing face) {
        this.pos = pos;
        this.face = face;
        this.hitVec = getHitVec();
    }

    private Vec3 getHitVec() {
        Vec3i directionVec = this.face.getDirectionVec();
        double x = directionVec.getX() * 0.5D;
        double z = directionVec.getZ() * 0.5D;

        if (this.face.getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE) {
            x = -x;
            z = -z;
        }

        Vec3 hitVec = (new Vec3(this.pos)).addVector(x + z, directionVec.getY() * 0.5D, x + z);

        Vec3 src = Minecraft.getMinecraft().thePlayer.getPositionEyes(1.0F);
        MovingObjectPosition obj = Minecraft.getMinecraft().theWorld.rayTraceBlocks(src,
                hitVec,
                false,
                false,
                true);

        if (obj == null || obj.hitVec == null || obj.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) {
            return null;
        }
        if (this.face != EnumFacing.DOWN && this.face != EnumFacing.UP) {
            obj.hitVec = obj.hitVec.addVector(0.0D, -0.2D, 0.0D);
        }
        return obj.hitVec;
    }
}
