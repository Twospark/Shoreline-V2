package net.shoreline.client.impl.config.impl;

import net.shoreline.client.api.module.Module;
import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.api.setting.impl.BindSetting;
import net.shoreline.client.api.setting.util.SettingContainer;
import net.shoreline.client.impl.Managers;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class BindConfig extends SettingContainerConfig<Module>
{
    public BindConfig(Path directory) throws IOException
    {
        super(directory, "binds", Managers.MODULES.getRegistry());
    }

    @Override
    public List<Setting<?>> getSettings(SettingContainer container)
    {
        return container.getSettings()
                .stream()
                .filter(setting -> setting instanceof BindSetting)
                .toList();
    }
}
