package net.shoreline.client.impl.gui.click;

import net.shoreline.client.api.gui.ShorelineGui;
import net.shoreline.client.api.gui.component.ParentComponent;
import net.shoreline.client.api.module.Category;

public class ClickScreen extends ShorelineGui
{
    public ClickScreen()
    {
        super("Modules");
    }

    @Override
    public void load()
    {
        for (Category category : Category.values())
        {
            ParentComponent component = new ParentComponent(category.name(), () -> true);
            component.setWidth(110);

            components.add(component);
        }
    }
}
