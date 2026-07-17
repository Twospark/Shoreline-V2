package net.shoreline.client.api.thread;

import net.shoreline.client.api.common.Feature;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class AsyncFeature extends Feature
{
    public AsyncFeature(String name)
    {
        super(name);
    }

    public AsyncFeature(String name, String[] nameAliases)
    {
        super(name, nameAliases);
    }

    public <T> Future<T> submitCallable(Callable<T> callable)
    {
        return ShorelineExecutor.SERVICE.submit(callable);
    }

    public Future<?> submit(Runnable runnable)
    {
        return ShorelineExecutor.SERVICE.submit(runnable);
    }
}
