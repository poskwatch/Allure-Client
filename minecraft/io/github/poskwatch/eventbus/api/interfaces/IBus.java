package io.github.poskwatch.eventbus.api.interfaces;

public interface IBus<IEvent> {

    void registerListener(IEventListener listener);
    void unregisterListener(IEventListener listener);

    void invokeEvent(IEvent event);

}
