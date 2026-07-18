package net.shoreline.client.api.gui.api;

import net.shoreline.client.api.gui.ShorelineGui;

/**
 * Interface for objects that can be
 * displayed in {@link ShorelineGui}.
 */
public interface Displayable
{
    GuiComponent getComponent();

}
