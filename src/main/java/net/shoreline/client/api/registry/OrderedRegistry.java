package net.shoreline.client.api.registry;

import lombok.Getter;
import net.shoreline.client.api.interfaces.Identifiable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class OrderedRegistry<T extends Identifiable> implements Registry<T>
{
    private final List<T> collection = new ArrayList<>();

    @Override
    public void register(T identifiable)
    {
        collection.add(identifiable);
    }

    @Override
    public void register(T... identifiable)
    {
        Arrays.stream(identifiable).forEach(this::register);
    }

    @Override
    public void unregister(T identifiable)
    {
        collection.remove(identifiable);
    }

    @Override
    public T get(String name)
    {
        for (T identifiable : collection)
        {
            if (identifiable.getId().equals(name))
            {
                return identifiable;
            }
        }

        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C extends T> C getByClass(Class<C> clazz)
    {
        for (T identifiable : collection)
        {
            if (identifiable.getClass() == clazz)
            {
                return (C) identifiable;
            }
        }

        return null;
    }
}