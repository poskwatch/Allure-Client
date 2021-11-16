package vip.allureclient.base.util.player;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import vip.allureclient.base.util.client.Wrapper;

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

}
