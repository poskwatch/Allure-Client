package vip.allureclient.impl.event.events.client;

import io.github.poskwatch.eventbus.api.interfaces.IEvent;

public class KeyPressedEvent implements IEvent {

    private final int key;

    public KeyPressedEvent(int key) {
        this.key = key;
    }

    public int getKey() {
        return key;
    }
}
