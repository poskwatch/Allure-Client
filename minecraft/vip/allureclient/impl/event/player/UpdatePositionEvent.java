package vip.allureclient.impl.event.player;

import vip.allureclient.base.event.CancellableEvent;
import vip.allureclient.base.util.client.Wrapper;

public class UpdatePositionEvent extends CancellableEvent {

    private double x, y, z;
    private float yaw, pitch;
    private boolean onGround;
    private boolean sprinting;
    private boolean pre;

    public UpdatePositionEvent(double x, double y, double z, float yaw, float pitch, boolean onGround, boolean sprinting){
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
        this.sprinting = sprinting;
        this.pre = true;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setYaw(float yaw, boolean visualize) {
        this.yaw = yaw;
        if (visualize) {
            Wrapper.getPlayer().renderYawOffset = yaw;
            Wrapper.getPlayer().rotationYawHead = yaw;
        }
    }

    public void setPitch(float pitch, boolean visualize) {
        this.pitch = pitch;
        if (visualize)
            Wrapper.getPlayer().rotationPitchHead = pitch;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public void setSprinting(boolean sprinting) {
        this.sprinting = sprinting;
    }

    public void setPre(boolean pre) {
        this.pre = pre;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public boolean isSprinting() {
        return sprinting;
    }

    public boolean isPre() {
        return pre;
    }
}
