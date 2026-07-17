package net.shoreline.eventbus.rewrite;

import net.shoreline.eventbus.rewrite.api.Listener;
import net.shoreline.eventbus.rewrite.api.Subscribe;
import net.shoreline.eventbus.rewrite.listener.LambdaFactoryListener;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings({"rawtypes", "unchecked"})
public class EventBus
{
    private static final EventBus INSTANCE = new EventBus();
    public static EventBus getInstance() { return INSTANCE; }

    /** A map events and their active listeners. */
    private final Map<Class<?>, List<Listener>> listeners;
    /** A map of objects and their corresponding listeners. */
    private final Map<Object, List<Listener>> corr;

    public EventBus()
    {
        this.listeners = new ConcurrentHashMap<>();
        this.corr      = new ConcurrentHashMap<>();
    }

    public void post(Object object)
    {
        List<Listener> listening = listeners.get(object.getClass());
        if (listening == null)
        {
            return;
        }

        Listener[] snapshot = listening.toArray(new Listener[0]);
        for (Listener listener : snapshot)
        {
            listener.invoke(object);
        }
    }

    public void post(Object object, Class<?> type)
    {
        List<Listener> listening = listeners.get(object.getClass());
        if (listening == null)
        {
            return;
        }

        Listener[] snapshot = listening.toArray(new Listener[0]);
        for (Listener listener : snapshot)
        {
            if (listener.getType() == type || listener.getType() == null)
            {
                listener.invoke(object);
            }
        }
    }

    public void subscribe(Object object)
    {
        Class<?> clazz = object.getClass();
        List<Listener> corresponding = corr.computeIfAbsent(
                object,
                _ -> new ArrayList<>());

        for (Method method : clazz.getDeclaredMethods())
        {
            if (!method.isAnnotationPresent(Subscribe.class))
            {
                continue;
            }

            Class<?>[] parameters = method.getParameterTypes();
            Listener<?> listener = new LambdaFactoryListener(
                    method,
                    parameters[0],
                    null,
                    object
            );

            corresponding.add(listener);
            List<Listener> list = listeners.computeIfAbsent(parameters[0],
                    v -> new ArrayList<>());
            addListener(listener, list);
        }
    }

    public void unsubscribe(Object object)
    {
        List<Listener> listening = corr.remove(object);
        if (listening == null)
        {
            return;
        }

        for (Listener listener : listening)
        {
            List<Listener> list = listeners.get(listener.getTarget());
            if (list != null)
            {
                list.remove(listener);
            }
        }
    }

    /**
     * Registers a listener without a subscriber.
     * Note that the listener will remain active
     * even when the object is unsubscribed.
     *
     * @param listener the listener to register.
     */
    public void register(Listener<?> listener)
    {
        register(null, listener);
    }

    public void register(Object object, Listener<?> listener)
    {
        if (object != null)
        {
            List<Listener> corresponding = corr.computeIfAbsent(
                    object,
                    _ -> new ArrayList<>());
            corresponding.add(listener);
        }

        List<Listener> list = listeners.computeIfAbsent(listener.getTarget(),
                _ -> new ArrayList<>());

        addListener(listener, list);
    }

    private void addListener(Listener<?> listener, List<Listener> list)
    {
        for (int i = 0; i < list.size(); i++)
        {
            int priority = list.get(i).getPriority();
            if (listener.getPriority() >= priority)
            {
                list.add(i, listener);
                return;
            }
        }

        list.add(listener);
    }
}
