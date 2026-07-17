package net.shoreline.client.api.setting.impl;

import com.google.gson.JsonElement;
import lombok.Getter;
import lombok.Setter;
import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.api.setting.SettingBuilder;

import java.awt.*;

@Setter
@Getter
public class ColorSetting extends Setting<Color>
{
    private boolean transparency;
    private boolean global;

    public ColorSetting(String name, String description)
    {
        super(name, description);
    }

    @Override
    public void fromJson(JsonElement element)
    {
        String value = element.getAsString();
        String[] split = value.split("-");

        int color = (int) Long.parseLong(split[0], 16);
        setValue(new Color(color));

        global = Boolean.parseBoolean(split[1]);
    }

    @Override
    public JsonElement toJson()
    {
        return parse(Integer.toHexString(getValue().getRGB()) + "-" + global);
    }

    public static class Builder extends SettingBuilder<Color>
    {
        private boolean transparency;
        private boolean global;

        public Builder(String name)
        {
            super(name);
        }

        public Builder setGlobalColor()
        {
            setDefaultValue(Color.WHITE);
            global = true;
            return this;
        }

        public Builder setRgb(int rgb)
        {
            setDefaultValue(new Color(rgb, (rgb & 0xff000000) != 0xff000000));
            return this;
        }

        public Builder setTransparency(boolean transparency)
        {
            this.transparency = transparency;
            return this;
        }

        @Override
        public Setting<Color> build()
        {
            ColorSetting built = (ColorSetting) super.build();
            built.setTransparency(transparency);
            if (global)
            {
                built.setGlobal(true);
            }

            return built;
        }
    }
}
