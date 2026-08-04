package net.shoreline.client.impl.command.util.impl;

import net.shoreline.client.api.setting.impl.BindSetting;
import net.shoreline.client.api.setting.util.Bind;
import net.shoreline.client.impl.command.util.ISettingParser;

public class BindSettingParser implements ISettingParser<Bind, BindSetting>
{
    @Override
    public boolean parseString(BindSetting setting, String string)
    {
        Bind bind = Bind.fromString(string);
        setting.setValue(bind);
        return true;
    }
}
