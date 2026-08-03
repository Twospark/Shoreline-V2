package net.shoreline.client.impl.modules.misc;

import net.shoreline.client.api.module.Category;
import net.shoreline.client.api.module.Toggleable;

public class SpammerModule extends Toggleable
{
    public SpammerModule()
    {
        super("Spammer", "Spams in chat", Category.MISCELLANEOUS);
    }
}
