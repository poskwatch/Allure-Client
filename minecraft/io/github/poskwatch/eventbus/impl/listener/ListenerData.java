package io.github.poskwatch.eventbus.impl.listener;

import io.github.poskwatch.eventbus.api.enums.Priority;
import io.github.poskwatch.eventbus.api.interfaces.IEvent;
import io.github.poskwatch.eventbus.api.interfaces.IEventCallable;
import io.github.poskwatch.eventbus.api.interfaces.IEventListener;

public class ListenerData {

    private final IEventListener listener;
    private final IEventCallable<IEvent> task;
    private final Priority priority;

    public ListenerData(IEventListener listener, IEventCallable<IEvent> task, Priority priority) {
        this.listener = listener;
        this.task = task;
        this.priority = priority;
    }

    public IEventListener getListener() {
        return listener;
    }

    public IEventCallable<IEvent> getTask() {
        return task;
    }

    public Priority getPriority() {
        return priority;
    }

}
