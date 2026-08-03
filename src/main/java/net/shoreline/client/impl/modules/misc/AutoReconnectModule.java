package net.shoreline.client.impl.modules.misc;

import net.shoreline.client.api.module.Category;
import net.shoreline.client.api.module.Toggleable;

/**
 * @see net.shoreline.client.asm.mixins.gui.screen.MixinDisconnectedScreen
 */
public class AutoReconnectModule extends Toggleable
{
    public static AutoReconnectModule INSTANCE;

    public AutoReconnectModule()
    {
        super("AutoReconnect", "Reconnects automatically after disconnect", Category.MISCELLANEOUS);
        INSTANCE = this;
    }
}