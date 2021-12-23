package vip.allureclient.base.bind;

import vip.allureclient.AllureClient;
import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.impl.event.client.KeyPressedEvent;

import java.util.HashMap;

public class BindManager<T extends Bindable> {

    private final HashMap<Integer, T> keyToObjectMap = new HashMap<>();

    @EventListener
    EventConsumer<KeyPressedEvent> onKeyPressEvent;

    public BindManager() {
        this.onKeyPressEvent = (keyPressEvent -> {
            if (keyToObjectMap.containsKey(keyPressEvent.getKey())) {
                keyToObjectMap.get(keyPressEvent.getKey()).onPressed();
            }
        });
        AllureClient.getInstance().getEventManager().subscribe(this);
    }

    public void registerBind(int bind, T register) {
        keyToObjectMap.put(bind, register);
    }
}
