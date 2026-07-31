package net.shoreline.client.api.setting.impl;

import com.google.gson.JsonElement;
import lombok.Getter;
import lombok.Setter;
import net.shoreline.client.api.gui.api.GuiComponent;
import net.shoreline.client.api.gui.component.NumberComponent;
import net.shoreline.client.api.gui.handler.NumberHandler;
import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.api.setting.SettingBuilder;

@Setter
@Getter
public class NumberSetting<N extends Number> extends Setting<N>
{
    private N min, max;
    private String format;
    private int roundingPlaces;

    public NumberSetting(String name, String description)
    {
        super(name, description);
    }

    @Override
    public void setValue(N value)
    {
        if (min != null && value.doubleValue() < min.doubleValue())
        {
            super.setValue(min);
        }
        else if (max != null && value.doubleValue() > max.doubleValue())
        {
            super.setValue(max);
        }
        else
        {
            super.setValue(value);
        }
    }

    @Override
    public void fromJson(JsonElement element)
    {
        setValue(getValue(element.getAsNumber()));
    }

    @Override
    public JsonElement toJson()
    {
        return parse(getValue().toString());
    }

    @SuppressWarnings("unchecked")
    public N getValue(Number number)
    {
        Class<? extends Number> type = getDefaultValue().getClass();
        Object result = null;

        if (type == Integer.class)
        {
            result = number.intValue();
        }
        else if (type == Float.class)
        {
            result = number.floatValue();
        }
        else if (type == Double.class)
        {
            result = number.doubleValue();
        }

        return (N) result;
    }

    @Override
    public GuiComponent getComponent()
    {
        NumberHandler<N> handler = new NumberHandler<>(
                this::getValue,
                this::setValue,
                this::getValue,
                roundingPlaces,
                min.doubleValue(),
                max.doubleValue());

        return new NumberComponent(getName(), getVisible(), handler, format == null ? "" : format);
    }

    public static class Builder<T extends Number> extends SettingBuilder<T>
    {
        private T min, max;
        private String format;
        private int roundingScale;

        public Builder(String name)
        {
            super(name);
        }

        @Override
        public Builder<T> setDefaultValue(T defaultValue)
        {
            super.setDefaultValue(defaultValue);
            this.roundingScale = defaultValue instanceof Float || defaultValue instanceof Double ? 1 : 0;
            return this;
        }

        public Builder<T> setMin(T min)
        {
            this.min = min;
            return this;
        }

        public Builder<T> setMax(T max)
        {
            this.max = max;
            return this;
        }

        public Builder<T> setFormat(String format)
        {
            this.format = format;
            return this;
        }

        public Builder<T> setRoundingScale(int roundingScale)
        {
            this.roundingScale = roundingScale;
            return this;
        }

        @Override
        @SuppressWarnings({"rawtypes", "unchecked"})
        public Setting<T> build()
        {
            NumberSetting build = (NumberSetting) super.build();
            if (min != null)
            {
                build.setMin(min);
            }

            if (max != null)
            {
                build.setMax(max);
            }

            build.setFormat(format);
            build.setRoundingPlaces(roundingScale);
            return build;
        }
    }
}
