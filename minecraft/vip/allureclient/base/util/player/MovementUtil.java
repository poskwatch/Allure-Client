package vip.allureclient.base.util.player;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.base.util.math.MathUtil;

public class MovementUtil {

    public static double getPlayerJumpHeight() {
        double baseJumpHeight = 0.41999998688697815D;
        if (Wrapper.getPlayer().isInLava() || Wrapper.getPlayer().isInWater()) {
            return 0.13500000163912773D;
        }
        if (Wrapper.getPlayer().isPotionActive(Potion.jump)) {
            return baseJumpHeight + ((Wrapper.getPlayer().getActivePotionEffect(Potion.jump).getAmplifier() + 1.0F) * 0.1F);
        }
        return baseJumpHeight;
    }

    public static double getPlayerBPS() {
        double lastDist;
        double xDist = Wrapper.getPlayer().posX - Wrapper.getPlayer().lastTickPosX;
        double zDist = Wrapper.getPlayer().posZ - Wrapper.getPlayer().lastTickPosZ;
        lastDist = StrictMath.sqrt(xDist * xDist + zDist * zDist);
        return MathUtil.roundToPlace(lastDist * 20 * Wrapper.getMinecraft().timer.timerSpeed, 2);
    }

    public static double getBaseMoveSpeed() {
        EntityPlayerSP player = Wrapper.getPlayer();
        double base = player.isSneaking() ? 0.06630000288486482D : 0.2872999905467033D;
        PotionEffect moveSpeed = player.getActivePotionEffect(Potion.moveSpeed);
        PotionEffect moveSlowness = player.getActivePotionEffect(Potion.moveSlowdown);
        if (moveSpeed != null) {
             base *= 1.0D + 0.2D * (moveSpeed.getAmplifier() + 1);
        }
        if (moveSlowness != null) {
            base *= 1.0D + 0.2D * (moveSlowness.getAmplifier() + 1);
        }
        if (player.isInWater()) {
            base *= 0.5203619984250619D;
        } else if (player.isInLava()) {
                base *= 0.5203619984250619D;
        }
        return base;
    }

    public static boolean isMoving() {
        return Wrapper.getPlayer().moveForward != 0 || Wrapper.getPlayer().moveStrafing != 0;
    }

    public static boolean isOnGround(double height) {
        return !Wrapper.getWorld().getCollidingBoundingBoxes(Wrapper.getPlayer(), Wrapper.getPlayer().getEntityBoundingBox().offset(0.0D, -height, 0.0D)).isEmpty();
    }

    public static BlockPos getBlockUnder() {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        return new BlockPos(player.posX, player.posY - 1.0D, player.posZ);
    }

    public static boolean isOverVoid() {
        for (double posY = (Wrapper.getPlayer()).posY; posY > 0.0D; posY--) {
            if (!(Wrapper.getWorld().getBlockState(new BlockPos((Wrapper.getPlayer()).posX, posY, (Wrapper.getPlayer()).posZ)).getBlock() instanceof net.minecraft.block.BlockAir)) {
                return false;
            }
        }
        return true;
    }

    public static void setSpeed(double speed) {
        double forward = Minecraft.getMinecraft().thePlayer.movementInput.moveForward;
        double strafe = Minecraft.getMinecraft().thePlayer.movementInput.moveStrafe;
        float yaw = Minecraft.getMinecraft().thePlayer.rotationYaw;
        if (forward == 0 && strafe == 0) {
            Wrapper.getPlayer().motionX = 0;
            Wrapper.getPlayer().motionZ = 0;
        } else {
            if (forward != 0) {
                if (strafe > 0) {
                    yaw += (forward > 0 ? -45 : 45);
                } else if (strafe < 0) {
                    yaw += (forward > 0 ? 45 : -45);
                }
                strafe = 0;
                if (forward > 0) {
                    forward = 1;
                } else {
                    forward = -1;
                }
            }
            double cos = Math.cos(Math.toRadians(yaw + 90));
            double sin = Math.sin(Math.toRadians(yaw + 90));
            Wrapper.getPlayer().motionX = forward * speed * cos + strafe * speed * sin;
            Wrapper.getPlayer().motionZ = forward * speed * sin - strafe * speed * cos;
        }
    }

    public static float getMovementDirection() {
        EntityPlayerSP player = Wrapper.getPlayer();
        float forward = player.moveForward;
        float strafe = player.moveStrafing;
        float direction = player.rotationYaw;
        if (forward < 0.0F) {
            direction += 180.0F;
            if (strafe > 0.0F) {
                direction += 45.0F;
            } else if (strafe < 0.0F) {
                direction -= 45.0F;
            }
        } else if (forward > 0.0F) {
            if (strafe > 0.0F) {
                direction -= 45.0F;
            } else if (strafe < 0.0F) {
                direction += 45.0F;
            }
        } else if (strafe > 0.0F) {
            direction -= 90.0F;
        } else if (strafe < 0.0F) {
            direction += 90.0F;
        }
        return direction;
    }

    public static void damagePlayer() {
        double x = Wrapper.getPlayer().posX;
        double y = Wrapper.getPlayer().posY;
        double z = Wrapper.getPlayer().posZ;
        float minValue = 3.1F;
        if (Wrapper.getPlayer().isPotionActive(Potion.jump)) {
            minValue += Wrapper.getPlayer().getActivePotionEffect(Potion.jump).getAmplifier() + 1.0F;
        }
        for (int i = 0; i < (int) ((minValue / (MathUtil.getRandomNumber(0.0890D, 0.0849D) - 1.0E-3D - Math.random() * 0.0002F - Math.random() * 0.0002F)) + 6); i++) {
            Wrapper.sendPacketDirect(new C03PacketPlayer.C04PacketPlayerPosition(x, y + MathUtil.getRandomNumber(0.0655D, 0.0625D) - MathUtil.getRandomNumber(1.0E-3D, 1.0E-2D) - Math.random() * 0.0002F, z, false));
            Wrapper.sendPacketDirect(new C03PacketPlayer.C04PacketPlayerPosition(x, y + Math.random() * 0.0002F, z, false));
        }
        Wrapper.sendPacketDirect(new C03PacketPlayer(true));
    }

    public static double getJumpHeight() {
        double baseJumpHeight = 0.41999998688697815D;
        if (Wrapper.getPlayer().isInWater() || Wrapper.getPlayer().isInLava())
            return 0.13500000163912773D;
        if (Wrapper.getPlayer().isPotionActive(Potion.jump)) {
            return baseJumpHeight + ((Wrapper.getPlayer().getActivePotionEffect(Potion.jump).getAmplifier() + 1.0F) * 0.1F);
        }
        return baseJumpHeight;
    }
}
