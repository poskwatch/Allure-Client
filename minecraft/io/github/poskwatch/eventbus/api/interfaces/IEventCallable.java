package io.github.poskwatch.eventbus.api.interfaces;

import java.util.function.Consumer;

@FunctionalInterface
public interface IEventCallable<T extends IEvent> extends Consumer<T> {
}
