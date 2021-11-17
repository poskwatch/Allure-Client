package vip.allureclient.impl.event.player;

import vip.allureclient.base.event.CancellableEvent;

public class PlayerStepEvent extends CancellableEvent {

    private double heightStepped, stepHeight;
    private boolean pre;

    public PlayerStepEvent(float stepHeight) {
        this.stepHeight = stepHeight;
        this.pre = true;
    }

    public double getHeightStepped() {
        return heightStepped;
    }

    public double getStepHeight() {
        return stepHeight;
    }

    public void setHeightStepped(double heightStepped) {
        this.heightStepped = heightStepped;
    }

    public void setStepHeight(double stepHeight) {
        this.stepHeight = stepHeight;
    }

    public boolean isPre() {
        return pre;
    }

    public void setPre(boolean pre) {
        this.pre = pre;
    }
}
