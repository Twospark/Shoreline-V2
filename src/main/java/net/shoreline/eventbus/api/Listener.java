package net.shoreline.eventbus.api;

/**
 * A listener.
 *
 * @param <T> the type of object this listener takes.
 */
public interface Listener<T> extends Invoker<T>
{
    /**
     * The listener priority is used to call listeners
     * in the correct order, usually from highest to lowest.
     *
     * @return this listeners' priority.
     */
    int getPriority();

    /** @return this listeners target. */
    Class<? super T> getTarget();

    /** @return The type of the target class. */
    Class<?> getType();
}
