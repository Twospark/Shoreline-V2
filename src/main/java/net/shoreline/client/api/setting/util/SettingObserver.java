package net.shoreline.client.api.setting.util;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SettingObserver<T>
{
    protected final List<Consumer<T>> observers = new ArrayList<>();

    public void onChange(T value)
    {
        observers.forEach(consumer -> consumer.accept(value));
    }

    public void addObserver(Consumer<T> observer)
    {
        observers.add(observer);
    }

    public void removeObserver(Consumer<T> observer)
    {
        observers.remove(observer);
    }
}
