package vip.allureclient.impl.event.player;

import vip.allureclient.base.event.CancellableEvent;

public class ChatMessageSendEvent extends CancellableEvent {

    private String message;

    public ChatMessageSendEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
