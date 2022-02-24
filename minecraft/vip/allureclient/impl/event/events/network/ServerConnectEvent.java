package vip.allureclient.impl.event.events.network;

import io.github.poskwatch.eventbus.api.interfaces.IEvent;

public class ServerConnectEvent implements IEvent {

    private final String ip;

    public ServerConnectEvent(String ip) {
        this.ip = ip;
    }

    public String getIP() {
        return ip;
    }
}
