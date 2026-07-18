package net.shoreline.client.impl.command.util.impl;

import net.shoreline.client.api.setting.impl.BooleanSetting;
import net.shoreline.client.impl.command.util.ISettingParser;

public class BooleanSettingParser implements ISettingParser<Boolean, BooleanSetting>
{
    @Override
    public boolean parseString(BooleanSetting setting, String string)
    {
        if (string.equalsIgnoreCase("true"))
        {
            setting.setValue(true);
            return true;
        }
        else if (string.equalsIgnoreCase("false"))
        {
            setting.setValue(false);
            return true;
        }
        else if (string.equalsIgnoreCase("toggle"))
        {
            setting.setValue(!setting.getValue());
            return true;
        }

        return false;
    }
}