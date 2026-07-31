package net.shoreline.client.impl.config;

import net.shoreline.client.Shoreline;
import net.shoreline.client.api.registry.OrderedRegistry;
import net.shoreline.client.api.registry.RegistryFeature;
import net.shoreline.client.impl.Managers;
import net.shoreline.client.impl.config.impl.BindConfig;
import net.shoreline.client.impl.config.impl.SettingContainerConfig;
import net.shoreline.client.impl.event.ClientEvent;
import net.shoreline.eventbus.EventBus;
import net.shoreline.eventbus.api.Subscribe;
import net.shoreline.loader.Loader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class ConfigManager extends RegistryFeature<AbstractConfig>
{
    private Path saveDirectory;

    public ConfigManager()
    {
        super("Configs", new OrderedRegistry<>());
        EventBus.getInstance().subscribe(this);

        Path runningDir = mc.gameDirectory.toPath();
        try
        {
            File homeDir = new File(System.getProperty("user.home"));
            saveDirectory = homeDir.toPath();
        }
        catch (Exception e)
        {
            Loader.error("Could not access home directory!");
            e.printStackTrace();
            saveDirectory = runningDir;
        }

        saveDirectory = saveDirectory.resolve("Shoreline");
        try
        {
            getRegistry().register(
                    new SettingContainerConfig<>(saveDirectory, "modules", Managers.MODULES.getRegistry()),
                    new BindConfig(saveDirectory));
        }
        catch (IOException e)
        {
            Loader.error("Failed to create client configurations", e);
        }
    }

    @Subscribe(priority = Integer.MIN_VALUE)
    public void onLoaded(ClientEvent.Loaded event)
    {
        loadAll();
    }

    @Subscribe
    public void onShutDown(ClientEvent.ShutDown event)
    {
        saveAll();
    }

    public void loadAll()
    {
        for (AbstractConfig config : getRegistry().getCollection())
        {
            try
            {
                config.loadFile();
            }
            catch (IOException e)
            {
                Loader.error("Failed to load config: " + config, e);
            }
        }
    }

    public void saveAll()
    {
        for (AbstractConfig config : getRegistry().getCollection())
        {
            try
            {
                config.saveFile();
            }
            catch (IOException e)
            {
                Loader.error("Failed to save config: " + config, e);
            }
        }
    }
}
