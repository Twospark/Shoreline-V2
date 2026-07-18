package net.shoreline.client.impl.command.util.impl;

import net.shoreline.client.api.setting.impl.EnumSetting;
import net.shoreline.client.impl.command.util.ISettingParser;

@SuppressWarnings("unchecked")
public class EnumSettingParser<E extends Enum<E>> implements ISettingParser<E, EnumSetting<E>>
{
    @Override
    public boolean parseString(EnumSetting<E> config, String string)
    {
        E value = (E) Enum.valueOf(((Enum<?>) config.getValue()).getDeclaringClass(), string.toUpperCase());
        config.setValue(value);
        return true;
    }
}