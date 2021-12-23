package vip.allureclient.impl.event.client;

import vip.allureclient.base.event.Event;

public class KeyPressedEvent implements Event {

    private final int key;

    public KeyPressedEvent(int key) {
        this.key = key;
    }

    public int getKey() {
        return key;
    }
}
