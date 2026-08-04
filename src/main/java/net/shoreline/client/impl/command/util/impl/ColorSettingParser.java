package net.shoreline.client.impl.command.util.impl;

import net.shoreline.client.api.setting.impl.ColorSetting;
import net.shoreline.client.impl.command.util.ISettingParser;

import java.awt.*;

public class ColorSettingParser implements ISettingParser<Color, ColorSetting>
{
    @Override
    public boolean parseString(ColorSetting setting, String string)
    {
        String hex = Integer.toHexString(setting.getValue().getRGB());
        setting.setValue(new Color((int) Long.parseLong(hex, 16), true));
        return true;
    }
}
