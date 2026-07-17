package net.shoreline.eventbus.listener;

import net.shoreline.eventbus.api.Listener;
import net.shoreline.eventbus.api.Subscribe;

import java.lang.reflect.Method;

/**
 * A simple implementation of the Listener interface.
 *
 * @param <T> the type of object this listener listens to.
 */
public abstract class AbstractEventListener<T> implements Listener<T>
{
    private final Class<? super T> target;
    private final Class<?> type;
    private final int priority;

    public AbstractEventListener(Class<? super T> target, int priority)
    {
        this(target, null, priority);
    }

    public AbstractEventListener(Class<? super T> target, Class<?> type)
    {
        this(target, type, 0);
    }

    public AbstractEventListener(Class<? super T> target, Class<?> type, int priority)
    {
        this.target = target;
        this.type = type;
        this.priority = priority;
    }

    /** Helper constructor for {@link LambdaFactoryListener} :p */
    public AbstractEventListener(Method listener, Class<? super T> target, Class<?> type)
    {
        Subscribe annotation = listener.getAnnotation(Subscribe.class);
        this.target = target;
        this.type = type;
        this.priority = annotation.priority();
    }

    @Override
    public int getPriority()
    {
        return priority;
    }

    @Override
    public Class<? super T> getTarget()
    {
        return target;
    }

    @Override
    public Class<?> getType()
    {
        return type;
    }
}
