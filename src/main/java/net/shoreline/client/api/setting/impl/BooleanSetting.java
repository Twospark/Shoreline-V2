package net.shoreline.client.api.setting.impl;

import com.google.gson.JsonElement;
import lombok.Getter;
import lombok.Setter;
import net.shoreline.client.api.gui.api.GuiComponent;
import net.shoreline.client.api.gui.component.BooleanComponent;
import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.api.setting.SettingBuilder;

@Getter
@Setter
public class BooleanSetting extends Setting<Boolean>
{
    protected boolean visibilityDependant;

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

    @Override
    public GuiComponent getComponent()
    {
        return new BooleanComponent(this);
    }

    public static class Builder extends SettingBuilder<Boolean>
    {
        public boolean visibilityDependant;

        public Builder(String name)
        {
            super(name);
        }

        public Builder setVisibilityDependant(boolean visibilityDependant)
        {
            this.visibilityDependant = visibilityDependant;
            return this;
        }

        @Override
        public Setting<Boolean> build()
        {
            BooleanSetting built = (BooleanSetting) super.build();
            built.setVisibilityDependant(visibilityDependant);
            return built;
        }
    }
}
