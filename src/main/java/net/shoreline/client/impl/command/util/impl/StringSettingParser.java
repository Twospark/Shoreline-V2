package net.shoreline.client.impl.command.util.impl;

import net.shoreline.client.api.setting.impl.StringSetting;
import net.shoreline.client.impl.command.util.ISettingParser;

public class StringSettingParser implements ISettingParser<String, StringSetting>
{
    @Override
    public boolean parseString(StringSetting setting, String string)
    {
        setting.setValue(string);
        return true;
    }
}
