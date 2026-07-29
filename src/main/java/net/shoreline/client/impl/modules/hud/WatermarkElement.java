package net.shoreline.client.impl.modules.hud;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.shoreline.client.ShorelineMod;
import net.shoreline.client.api.element.Element;
import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.api.setting.impl.BooleanSetting;
import net.shoreline.client.impl.Managers;

public class WatermarkElement extends Element
{
    Setting<Boolean> version = new BooleanSetting.Builder("Version")
            .setDescription("If we should display the version or not")
            .setDefaultValue(true).build();

    public WatermarkElement()
    {
        super("Watermark", "Displays the clients watermark", 2, 2);
    }

    @Override
    public void draw(GuiGraphicsExtractor graphics, float partialTicks)
    {
        drawText(graphics, getWatermark(), getX(), getY());
    }

    @Override
    public float getWidth()
    {
        return Managers.TEXT.getWidth(getWatermark());
    }

    @Override
    public float getHeight()
    {
        return Managers.TEXT.getHeight();
    }

    public String getWatermark()
    {
        return "Shoreline" + (version.getValue() ? ChatFormatting.WHITE + " v" + ShorelineMod.getSimpleVersion() : "");
    }
}
