package net.shoreline.client.api.thread;

import lombok.Getter;
import net.shoreline.client.api.common.Feature;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Getter
public class AsyncFeature<T> extends Feature
{
    protected Future<T> currentResult;
    protected final T defaultValue;

    public AsyncFeature(String name)
    {
        this(name, null);
    }

    public AsyncFeature(String name, T defaultValue)
    {
        super(name);
        this.defaultValue = defaultValue;
    }

    public AsyncFeature(String name, String[] nameAliases, T defaultValue)
    {
        super(name, nameAliases);
        this.defaultValue = defaultValue;
    }

    public Future<T> submitCallable(Callable<T> callable)
    {
        return currentResult =  ShorelineExecutor.SERVICE.submit(callable);
    }

    public Future<?> submit(Runnable runnable)
    {
        return ShorelineExecutor.SERVICE.submit(runnable);
    }

    public void cancelRun()
    {
        if (currentResult != null)
        {
            currentResult.cancel(false);
            currentResult = null;
        }
    }

    public T getResult()
    {
        if (currentResult == null || !currentResult.isDone())
        {
            return defaultValue;
        }

        try
        {
            return currentResult.get();
        }
        catch (ExecutionException | InterruptedException e)
        {
            e.printStackTrace();
            return defaultValue;
        }
    }
}
