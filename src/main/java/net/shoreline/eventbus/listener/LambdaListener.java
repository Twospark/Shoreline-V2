package net.shoreline.eventbus.listener;

import net.shoreline.eventbus.api.Invoker;

public class LambdaListener<T> extends AbstractEventListener<T>
{
    private final Invoker<T> invoker;

    public LambdaListener(Class<? super T> target, Invoker<T> invoker)
    {
        this(target, null, 0, invoker);
    }

    public LambdaListener(Class<? super T> target, int priority, Invoker<T> invoker)
    {
        this(target, null, priority, invoker);
    }

    public LambdaListener(Class<? super T> target, Class<?> type, Invoker<T> invoker)
    {
        this(target, type, 0, invoker);
    }

    public LambdaListener(Class<? super T> target, Class<?> type, int priority, Invoker<T> invoker)
    {
        super(target, type, priority);
        this.invoker = invoker;
    }

    @Override
    public void invoke(T event)
    {
        invoker.invoke(event);
    }
}
