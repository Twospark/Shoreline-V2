package net.shoreline.eventbus.listener;

import net.shoreline.eventbus.api.Invoker;
import net.shoreline.eventbus.api.Listener;
import net.shoreline.eventbus.api.Subscribe;

import java.lang.invoke.*;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An implementation of the Listener interface backed by a {@link LambdaMetafactory}.
 * <p>
 * Used mainly for registering listeners that are annotated with a {@link Subscribe}.
 * Very fast and efficient.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class LambdaFactoryListener<T> extends AbstractEventListener<T>
{
    /** A cache for our methods so we don't need to create a new invoker everytime a listener is registered. */
    private static final Map<Method, Invoker> CACHE = new ConcurrentHashMap<>();
    private final Invoker<T> invoker;

    public LambdaFactoryListener(Method method, Class<? super T> clazz, Class<?> targetType, Object subscriber)
    {
        super(method, clazz, targetType);
        this.invoker = CACHE.computeIfAbsent(method, listener ->
        {
            try
            {
                MethodHandles.Lookup lookup = MethodHandles.lookup();
                MethodType factory  = MethodType.methodType(Listener.class, subscriber.getClass());
                MethodType type     = MethodType.methodType(void.class, Object.class);
                MethodType target   = MethodType.methodType(void.class, method.getParameters()[0].getType());
                MethodHandle handle = lookup.unreflect(listener);

                CallSite site = LambdaMetafactory.metafactory(
                        lookup,
                        "invoke",
                        factory,
                        type,
                        handle,
                        target
                );

                return (Listener) site.getTarget().invoke(subscriber);
            }
            catch (Throwable e)
            {
                throw new RuntimeException("Couldn't create Listener for: " + method + listener);
            }
        });
    }

    @Override
    public void invoke(T event)
    {
        invoker.invoke(event);
    }
}
