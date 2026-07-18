package net.shoreline.client.api.registry;

import java.util.Collection;

public interface Registry<T>
{
    void register(T identifiable);

    void register(T...identifiable);

    void unregister(T identifiable);

    T get(String name);

    <C extends T> C getByClass(Class<C> clazz);

    Collection<T> getCollection();
}
