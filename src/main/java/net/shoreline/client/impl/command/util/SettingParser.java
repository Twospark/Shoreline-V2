package net.shoreline.client.impl.command.util;

import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.api.setting.impl.*;
import net.shoreline.client.impl.command.util.impl.BooleanSettingParser;
import net.shoreline.client.impl.command.util.impl.EnumSettingParser;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"unchecked", "rawtypes"})
public class SettingParser
{
    private static final Map<Class<? extends Setting>,
                ISettingParser<?, ?>> PARSERS = new HashMap<>();

    static
    {
        PARSERS.put(BooleanSetting.class,         new BooleanSettingParser());
        PARSERS.put(EnumSetting.class,            new EnumSettingParser<>());
    }

    @SuppressWarnings("unchecked")
    public static <T> boolean parseString(Setting<T> setting, String string)
    {
        ISettingParser<T, Setting<T>> parser =
                (ISettingParser<T, Setting<T>>) PARSERS.get(setting.getClass());

        if (parser != null)
        {
            return parser.parseString(setting, string);
        }

        return false;
    }
}
