package vip.allureclient.impl.event.visual;

import net.minecraft.client.gui.ScaledResolution;
import vip.allureclient.base.event.CancellableEvent;

public class Render2DEvent extends CancellableEvent {

    private final float partialTicks;
    private final ScaledResolution sr;

    public Render2DEvent(float partialTicks, ScaledResolution sr){
        this.partialTicks = partialTicks;
        this.sr = sr;
    }

    public ScaledResolution getScaledResolution() {
        return sr;
    }

    public float getPartialTicks() {
        return partialTicks;
    }
}
