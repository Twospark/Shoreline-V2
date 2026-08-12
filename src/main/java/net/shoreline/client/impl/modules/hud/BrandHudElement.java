package net.shoreline.client.impl.modules.hud;

import net.shoreline.client.api.element.dynamic.DynamicElement;
import net.shoreline.client.api.element.dynamic.DynamicEntry;

public class BrandHudElement extends DynamicElement
{
    public BrandHudElement()
    {
        super("Brand", "Displays the server brand", 200, 300);
    }

    @Override
    public void loadEntries()
    {
        getEntries().add(new DynamicEntry(this, this::getBrandText, () -> true));
    }

    public String getBrandText()
    {
        return mc.player.connection.serverBrand();
    }
}
