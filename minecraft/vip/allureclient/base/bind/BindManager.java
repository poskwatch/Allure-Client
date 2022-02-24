package vip.allureclient.base.bind;

import io.github.poskwatch.eventbus.api.annotations.EventHandler;
import io.github.poskwatch.eventbus.api.enums.Priority;
import io.github.poskwatch.eventbus.api.interfaces.IEventCallable;
import io.github.poskwatch.eventbus.api.interfaces.IEventListener;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.impl.event.events.client.KeyPressedEvent;

import java.util.HashMap;

public class BindManager<T extends BindableObject> {

    private final HashMap<Integer, T> keyToObjectMap = new HashMap<>();
    private final HashMap<Integer, Runnable> keyToActionMap = new HashMap<>();

    public BindManager() {
        Wrapper.getEventBus().registerListener(new IEventListener() {
            @EventHandler(events = KeyPressedEvent.class, priority = Priority.VERY_HIGH)
            final IEventCallable<KeyPressedEvent> onKeyPressed = (event -> {
                if (keyToObjectMap.containsKey(event.getKey()))
                    keyToObjectMap.get(event.getKey()).onPressed();
                if (keyToActionMap.containsKey(event.getKey()))
                    keyToActionMap.get(event.getKey()).run();
            });
        });
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
