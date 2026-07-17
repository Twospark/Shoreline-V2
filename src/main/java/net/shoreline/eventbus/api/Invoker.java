package net.shoreline.eventbus.api;

public interface Invoker<T>
{
    void invoke(T event);

}
