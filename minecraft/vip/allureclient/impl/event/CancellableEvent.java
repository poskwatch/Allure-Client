package vip.allureclient.impl.event;

import io.github.poskwatch.eventbus.api.interfaces.IEvent;

public class CancellableEvent implements IEvent {
    private boolean cancelled;

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public void setCancelled() {
        this.cancelled = true;
    }
}
