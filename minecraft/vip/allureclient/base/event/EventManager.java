package vip.allureclient.base.event;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class EventManager<Event> {
    private final Map<Type, List<CallSite<Event>>> callSiteMap = new HashMap<>();
    private final Map<Type, List<Listener<Event>>> listenerCache = new HashMap<>();
    public final void subscribe(Object subscriber) {
        byte b;
        int i;
        Field[] arrayOfField;
        for (i = (arrayOfField = subscriber.getClass().getDeclaredFields()).length, b = 0; b < i; ) {
            Field field = arrayOfField[b];
            if (field.isAnnotationPresent(EventListener.class)) {
                Type eventType = ((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0];
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                try {
                    byte priority;
                    Listener<Event> listener = (Listener<Event>) field.get(subscriber);
                     priority = 2;
                     if (this.callSiteMap.containsKey(eventType)) {
                        List<CallSite<Event>> callSites = this.callSiteMap.get(eventType);
                        callSites.add(new CallSite<>(subscriber, listener, priority));
                        callSites.sort((o1, o2) -> o2.priority - o1.priority);
                     }
                     else {
                        this.callSiteMap.put(eventType, new ArrayList<>(Arrays.asList((CallSite<Event>[])new CallSite[] {new CallSite<>(subscriber, listener, priority) })));
                     }
                }
                catch (IllegalAccessException ignored) {

                }
            }
            b++;
        }
        populateListenerCache();
    }

    private void populateListenerCache() {
        Map<Type, List<CallSite<Event>>> callSiteMap = this.callSiteMap;
        Map<Type, List<Listener<Event>>> listenerCache = this.listenerCache;
        for (Type type : callSiteMap.keySet()) {
            List<CallSite<Event>> callSites = callSiteMap.get(type);
            int size = callSites.size();
            List<Listener<Event>> listeners = new ArrayList<>(size);
            for (CallSite<Event> callSite : callSites)
                listeners.add(callSite.listener);
                listenerCache.put(type, listeners);
        }
    }

    public final void unsubscribe(Object subscriber) {
        for (List<CallSite<Event>> callSites : this.callSiteMap.values()) {
            callSites.removeIf(eventCallSite -> (eventCallSite.owner == subscriber));
        }
        populateListenerCache();
    }

    public final void post(Event event) {
        List<Listener<Event>> listeners = this.listenerCache.get(event.getClass());
        if (listeners != null)
            for (Listener<Event> listener : listeners) {
                listener.call(event);
            }
    }
    private static class CallSite<Event> {
        private final Object owner;
        private final Listener<Event> listener;
        private final byte priority;
        public CallSite(Object owner, Listener<Event> listener, byte priority) {
            this.owner = owner;
            this.listener = listener;
            this.priority = priority;
        }
    }
}
