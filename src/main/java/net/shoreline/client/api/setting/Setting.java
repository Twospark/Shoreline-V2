package net.shoreline.client.api.setting;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.shoreline.client.api.config.ConfigElement;
import net.shoreline.client.api.interfaces.Identifiable;
import net.shoreline.client.api.setting.util.SettingObserver;

import java.util.function.Supplier;

@RequiredArgsConstructor
@Getter
@Setter
public abstract class Setting<T> extends SettingObserver<T> implements Identifiable, ConfigElement
{
    protected final String name;
    protected final String description;

    protected Supplier<Boolean> visible;
    protected String[] aliases;
    protected T value;
    protected T defaultValue;
    protected boolean grouped;

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String[] getAliases()
    {
        return aliases;
    }

    public void setValue(T value)
    {
        this.value = value;
        onChange(value);
    }

    public boolean isVisible()
    {
        return visible != null ? visible.get() : true;
    }
}
