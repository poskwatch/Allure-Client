package vip.allureclient.base.event;

@FunctionalInterface
public interface Listener<Event> {
    void call(Event paramEvent);
}
