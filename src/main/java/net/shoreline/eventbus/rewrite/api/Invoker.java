package net.shoreline.eventbus.rewrite.api;

public interface Invoker<T>
{
    void invoke(T event);

}
