package net.shoreline.client.api.setting.impl;

import com.google.gson.JsonElement;
import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.api.setting.SettingBuilder;

public class BooleanSetting extends Setting<Boolean>
{
    public BooleanSetting(String name, String description)
    {
        super(name, description);
    }

    @Override
    public void fromJson(JsonElement element)
    {
        setValue(element.getAsBoolean());
    }

    @Override
    public JsonElement toJson()
    {
        return parse(getValue().toString());
    }

    public static class Builder extends SettingBuilder<Boolean>
    {
        public Builder(String name)
        {
            super(name);
        }
    }
}
