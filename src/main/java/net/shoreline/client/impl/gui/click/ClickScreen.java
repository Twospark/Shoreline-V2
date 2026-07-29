package net.shoreline.client.impl.gui.click;

import net.shoreline.client.api.gui.ShorelineGui;
import net.shoreline.client.api.gui.component.WindowComponent;
import net.shoreline.client.api.module.Category;
import net.shoreline.client.api.module.Module;
import net.shoreline.client.impl.Managers;
import net.shoreline.client.util.Formatter;

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
            if (category == Category.HUD)
            {
                continue;
            }

            String formatted = Formatter.capitalize(category.name().toLowerCase());
            WindowComponent component = new WindowComponent(formatted, searchHandler);
            for (Module module : Managers.MODULES.getModules(
                    module -> module.getCategory() == category))
            {
                component.getComponents().add(module.getComponent());
            }

            components.add(component);
        }
    }
}
