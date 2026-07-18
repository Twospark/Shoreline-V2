package net.shoreline.client.impl.command.util;

import net.shoreline.client.api.setting.Setting;

public interface ISettingParser<T, S extends Setting<T>>
{
    boolean parseString(S setting, String string);
}
