package net.shoreline.client.api.thread;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.experimental.UtilityClass;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * This class represents the clients {@link ExecutorService} we use for MultiThreading.
 */
@UtilityClass
public class ShorelineExecutor
{
    private final ThreadFactory FACTORY = new ThreadFactoryBuilder()
            .setDaemon(true)
            .setNameFormat("Shoreline-Thread-%d")
            .build();

    public final ExecutorService SERVICE = Executors.newCachedThreadPool(FACTORY);
}