package io.github.poskwatch.eventbus.impl.bus;

import io.github.poskwatch.eventbus.api.annotations.EventHandler;
import io.github.poskwatch.eventbus.api.interfaces.IBus;
import io.github.poskwatch.eventbus.api.interfaces.IEvent;
import io.github.poskwatch.eventbus.api.interfaces.IEventCallable;
import io.github.poskwatch.eventbus.api.interfaces.IEventListener;
import io.github.poskwatch.eventbus.impl.listener.ListenerData;

import java.lang.reflect.Field;
import java.util.*;

public class EventBus implements IBus<IEvent> {

    private final Map<Class<? extends IEvent>, List<ListenerData>> dataListToClassMap;
    private final Map<Class<? extends IEvent>, List<IEventCallable<IEvent>>> callableMapCache;

    public EventBus() {
        this.dataListToClassMap = new HashMap<>();
        this.callableMapCache = new HashMap<>();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void registerListener(IEventListener listener) {
        // Iterate through listener's class' fields to find annotated callables.
        for (Field field : listener.getClass().getDeclaredFields()) {
            // If the field has EventHandler annotation, and is an instance of IEventCallable, register via map.
            if (field.isAnnotationPresent(EventHandler.class) && field.getType().equals(IEventCallable.class)) {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                // Create Listener Data for field, catching IllegalAccessException.
                try {
                    EventHandler handlerAnnotation = field.getAnnotation(EventHandler.class);
                    ListenerData fieldListenerData = new ListenerData(listener, (IEventCallable<IEvent>) field.get(listener), handlerAnnotation.priority());
                    // Iterate through EventHandler classes to add data to map
                    for (Class<? extends IEvent> eventClass : handlerAnnotation.events()) {
                        // If map contains the class already, add the listener data to class, then sort by priority.
                        if (dataListToClassMap.containsKey(eventClass)) {
                            dataListToClassMap.get(eventClass).add(fieldListenerData);
                            dataListToClassMap.get(eventClass).sort(Comparator.comparingInt(list -> ((ListenerData) list).getPriority().getPriority()).reversed());
                        }
                        // Otherwise, put the data into a list with the class.
                        else {
                            ArrayList<ListenerData> dataArrayList = new ArrayList<ListenerData>() {{
                                add(fieldListenerData);
                            }};
                            dataListToClassMap.put(eventClass, dataArrayList);
                        }
                    }
                }
                catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        // Populate callable cache to update class to callable map.
        this.populateCallableCache();
    }

    @Override
    public void unregisterListener(IEventListener listener) {
        // Create list that inherits list to class map values.
        List<List<ListenerData>> listenerDataList = new ArrayList<>(dataListToClassMap.values());
        // Create index integer for map iteration.
        int i = listenerDataList.size();
        // Iterate using created index.
        while (i > 0)
            // Remove if the listener data object is equal to the unregistering object.
            listenerDataList.get(--i).removeIf(listenerData -> listenerData.getListener().equals(listener));
        // Populate callable cache to update class to callable map.
        this.populateCallableCache();
    }

    @Override
    public void invokeEvent(IEvent event) {
        // Use executor to call events within the data map. Iterates through lists and accepts each handler within it.
        List<IEventCallable<IEvent>> registeredCallableList = callableMapCache.get(event.getClass());
        if (registeredCallableList != null) {
            int i = registeredCallableList.size();
            while (i > 0)
                // Call each callable event
                registeredCallableList.get(--i).accept(event);
        }
    }

    private void populateCallableCache() {
        for (Class<? extends IEvent> eventClass : this.dataListToClassMap.keySet()) {
            // Create empty list for callables.
            final List<IEventCallable<IEvent>> callableList = new ArrayList<>();
            // Create data list to class map list.
            final List<ListenerData> listenerDataList = this.dataListToClassMap.get(eventClass);
            // Sort listener data list.
            listenerDataList.sort(Comparator.comparingInt(listenerData -> listenerData.getPriority().getPriority()));
            // Represents size of ListenerData list within main map.
            int i = listenerDataList.size();
            // Iterate through main map, adding IEventCallable from ListenerData.
            while (i > 0)
                callableList.add(listenerDataList.get(--i).getTask());
            // Put callableList with class.
            this.callableMapCache.put(eventClass, callableList);
        }
    }
}
