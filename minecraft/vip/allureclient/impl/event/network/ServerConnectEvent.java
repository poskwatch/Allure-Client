package vip.allureclient.impl.event.network;

import vip.allureclient.base.event.Event;

public class ServerConnectEvent implements Event {

    private final String ip;

    public ServerConnectEvent(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }
}
