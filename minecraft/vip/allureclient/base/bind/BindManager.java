package vip.allureclient.base.bind;

import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.impl.event.client.KeyPressedEvent;

import java.util.HashMap;

public class BindManager<T extends BindableObject> {

    private final HashMap<Integer, T> keyToObjectMap = new HashMap<>();
    private final HashMap<Integer, Runnable> keyToActionMap = new HashMap<>();

    @EventListener
    EventConsumer<KeyPressedEvent> onKeyPressEvent;

    public BindManager() {
        this.onKeyPressEvent = (keyPressEvent -> {
            if (keyToObjectMap.containsKey(keyPressEvent.getKey())) {
                keyToObjectMap.get(keyPressEvent.getKey()).onPressed();
            }
            if (keyToActionMap.containsKey(keyPressEvent.getKey())) {
                keyToActionMap.get(keyPressEvent.getKey()).run();
            }
        });
        Wrapper.getEventManager().subscribe(this);
    }

    public void registerBind(int bind, T register) {
        keyToObjectMap.remove(bind);
        keyToObjectMap.put(bind, register);
    }

    public void registerBind(int bind, Runnable register) {
        keyToActionMap.put(bind, register);
    }

    public void unbind(T object) {
        keyToObjectMap.remove(object.getBind());
    }

    public void unbind(Runnable runnable, Integer bind) {
        keyToActionMap.remove(bind, runnable);
    }
}
