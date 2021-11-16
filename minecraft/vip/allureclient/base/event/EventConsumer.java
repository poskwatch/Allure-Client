package vip.allureclient.base.event;

@FunctionalInterface
public interface EventConsumer<Event> {
    void call(Event paramEvent);
}
