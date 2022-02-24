package vip.allureclient.impl.event.events.visual;

import io.github.poskwatch.eventbus.api.interfaces.IEvent;

public class Render3DEvent implements IEvent {

    private final float partialTicks;

    public Render3DEvent(float partialTicks){
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return partialTicks;
    }
}
