package net.shoreline.client.util.input;

import lombok.experimental.UtilityClass;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;

@UtilityClass
public class InputUtil
{
    private final Options options = Minecraft.getInstance().options;

    public boolean isInputtingHotbar()
    {
        for (KeyMapping mapping : options.keyHotbarSlots)
        {
            if (mapping.isDown())
            {
                return true;
            }
        }

        return false;
    }
}
