package net.shoreline.client.api.setting.impl;

import com.google.gson.JsonElement;
import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.api.setting.SettingBuilder;
import net.shoreline.client.api.setting.util.Bind;

public class BindSetting extends Setting<Bind>
{
    public BindSetting(String name, String description)
    {
        super(name, description);
    }

    @Override
    public void fromJson(JsonElement element)
    {

    }

    @Override
    public JsonElement toJson()
    {
        return null;
    }

    public static class Builder extends SettingBuilder<Bind>
    {
        public Builder(String name)
        {
            super(name);
        }
    }
}
