package net.shoreline.client.impl.command.util.impl;

import net.shoreline.client.api.setting.impl.NumberSetting;
import net.shoreline.client.impl.command.util.ISettingParser;

import java.math.BigDecimal;

public class NumberSettingParser<N extends Number>
    implements ISettingParser<N, NumberSetting<N>>
{
    @Override
    public boolean parseString(NumberSetting<N> setting, String string)
    {
        N converted = convert(setting, new BigDecimal(string));
        setting.setValue(converted);
        return true;
    }

    @SuppressWarnings("unchecked")
    public N convert(NumberSetting<N> config, Number number)
    {
        Class<? extends Number> type = config.getValue().getClass();
        if (type == Integer.class)
        {
            return (N) Integer.valueOf(number.intValue());
        }
        else if (type == Float.class)
        {
            return (N) Float.valueOf(number.floatValue());
        }
        else if (type == Double.class)
        {
            return (N) Double.valueOf(number.doubleValue());
        }

        throw new IllegalArgumentException("Unsupported number type: " + type);
    }
}
