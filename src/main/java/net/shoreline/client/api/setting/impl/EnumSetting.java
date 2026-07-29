package net.shoreline.client.api.setting.impl;

import com.google.gson.JsonElement;
import net.shoreline.client.api.gui.api.GuiComponent;
import net.shoreline.client.api.gui.component.EnumComponent;
import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.api.setting.SettingBuilder;
import net.shoreline.client.impl.command.util.SettingParser;

public class EnumSetting<E extends Enum<E>> extends Setting<E>
{
    public EnumSetting(String name, String description)
    {
        super(name, description);
    }

    @Override
    public void fromJson(JsonElement element)
    {
        SettingParser.parseString(this, element.getAsString());
    }

    @Override
    public JsonElement toJson()
    {
        return parse(getValue().toString());
    }

    @Override
    public GuiComponent getComponent()
    {
        return new EnumComponent<>(this);
    }

    public static class Builder<E extends Enum<E>> extends SettingBuilder<E>
    {
        public Builder(String name)
        {
            super(name);
        }

        @Override
        public Setting<E> build()
        {
            return buildInternal(new EnumSetting<>(name, description));
        }
    }
}