package net.shoreline.client.impl.modules.client;

import lombok.Getter;
import net.shoreline.client.api.module.Category;
import net.shoreline.client.api.module.Toggleable;
import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.api.setting.impl.BooleanSetting;
import net.shoreline.client.api.setting.impl.NumberSetting;
import net.shoreline.client.api.setting.impl.StringSetting;
import net.shoreline.client.impl.Managers;
import net.shoreline.client.impl.event.ClientEvent;
import net.shoreline.eventbus.api.Subscribe;

@Getter
public class FontModule extends Toggleable
{
    public static FontModule INSTANCE;

    Setting<String> fontName = new StringSetting.Builder("Font")
            .setDescription("The font of the client")
            .setDefaultValue("Verdana").build();
    Setting<Integer> fontSize = new NumberSetting.Builder<Integer>("FontSize")
            .setMin(5).setMax(15).setDefaultValue(9)
            .setDescription("The size of the font").build();
    Setting<Boolean> antiAlias = new BooleanSetting.Builder("AntiAlias")
            .setDescription("Applies AA texturing on font")
            .setDefaultValue(true).build();
    Setting<Boolean> fractionalMetrics = new BooleanSetting.Builder("FractionalMetrics")
            .setDescription("Applies fractional metrics on font")
            .setDefaultValue(false).build();
    Setting<Float> fontOffset = new NumberSetting.Builder<Float>("FontOffset")
            .setMin(-5f).setMax(5f).setDefaultValue(-3f)
            .setDescription("Offset for fonts that dont align properly").build();

    public FontModule()
    {
        super("Font", "Client custom fonts", Category.CLIENT);
        INSTANCE = this;

        fontName.addObserver(this::setFont);
        fontSize.addObserver(this::setFont);

        antiAlias.addObserver(v -> close());
        fractionalMetrics.addObserver(v -> close());
    }

    @Subscribe
    public void onLoad(ClientEvent.McLoaded event)
    {
        Managers.TEXT.setRenderer(fontName.getValue(), fontSize.getValue());
    }

    public void close()
    {
        Managers.TEXT.getRenderer().close();
    }

    public void setFont(String fontName)
    {
        Managers.TEXT.setRenderer(fontName, fontSize.getValue());
    }

    public void setFont(int size)
    {
        Managers.TEXT.setRenderer(fontName.getValue(), size);
    }
}
