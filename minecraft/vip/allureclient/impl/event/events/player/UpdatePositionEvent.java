package vip.allureclient.impl.event.events.player;

import net.minecraft.client.Minecraft;
import vip.allureclient.impl.event.CancellableEvent;

import java.beans.ConstructorProperties;

public class UpdatePositionEvent extends CancellableEvent {

    private double x, y, z;
    private float yaw, pitch;
    private boolean onGround;
    private boolean pre;

    @ConstructorProperties({"x", "y", "z", "yaw", "pitch", "onGround"})
    public UpdatePositionEvent(double x, double y, double z, float yaw, float pitch, boolean onGround){
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
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
            Minecraft.getMinecraft().thePlayer.renderYawOffset = yaw;
            Minecraft.getMinecraft().thePlayer.rotationYawHead = yaw;
        }
    }

    public void setPitch(float pitch, boolean visualize) {
        this.pitch = pitch;
        if (visualize)
            Minecraft.getMinecraft().thePlayer.rotationPitchHead = pitch;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
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

    public boolean isPre() {
        return pre;
    }
}
