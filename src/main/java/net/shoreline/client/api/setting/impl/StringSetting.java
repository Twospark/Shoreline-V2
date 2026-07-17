package net.shoreline.client.api.setting.impl;

import com.google.gson.JsonElement;
import lombok.Setter;
import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.api.setting.SettingBuilder;

@Setter
public class StringSetting extends Setting<String>
{
    /** Restriction for value length. A value of -1 means no restriction */
    private int restriction;

    public StringSetting(String name, String description)
    {
        super(name, description);
    }

    @Override
    public void fromJson(JsonElement element)
    {
        setValue(element.getAsString());
    }

    @Override
    public JsonElement toJson()
    {
        return parse(getValue() == null ? "null" : "\"" + getValue().replace("\\", "\\\\") + "\"");
    }

    @Override
    public void setValue(String value)
    {
        if (inBounds(value))
        {
            super.setValue(value);
        }
    }

    public boolean inBounds(String string)
    {
        if (string == null)
        {
            return false;
        }

        return inBounds(string.length());
    }

    public boolean inBounds(int length)
    {
        if (restriction == -1)
        {
            return true;
        }

        return length <= restriction;
    }

    public boolean hasRestriction()
    {
        return restriction != -1;
    }

    public static class Builder extends SettingBuilder<String>
    {
        private int restriction = -1;

        public Builder(String name)
        {
            super(name);
        }

        public Builder withRestriction(int restriction)
        {
            this.restriction = restriction;
            return this;
        }

        @Override
        public Setting<String> build()
        {
            StringSetting setting = (StringSetting) super.build();
            setting.setRestriction(restriction);
            return setting;
        }
    }
}
