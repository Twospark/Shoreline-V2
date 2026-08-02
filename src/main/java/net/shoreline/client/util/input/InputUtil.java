package net.shoreline.client.util.input;

import lombok.experimental.UtilityClass;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.util.Mth;

import java.util.Arrays;

@UtilityClass
public class InputUtil
{
    private final Options options = Minecraft.getInstance().options;

    public KeyMapping[] getMovementKeys()
    {
        return new KeyMapping[]
        {
            options.keyUp,
            options.keyDown,
            options.keyLeft,
            options.keyRight
        };
    }

    public boolean isInputtingMovement()
    {
        return Arrays.stream(getMovementKeys()).anyMatch(KeyMapping::isDown);
    }


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

    public float getYawFromInput(float yaw)
    {
        boolean forward = options.keyUp.isDown();
        boolean backward = options.keyDown.isDown();
        boolean left = options.keyLeft.isDown();
        boolean right = options.keyRight.isDown();

        if (forward && !backward)
        {
            if (left && !right)
            {
                yaw -= 45.0f;
            }
            else if (right && !left)
            {
                yaw += 45.0f;
            }
        }
        else if (backward && !forward)
        {
            yaw += 180.0f;
            if (left && !right)
            {
                yaw += 45.0f;
            }
            else if (right && !left)
            {
                yaw -= 45.0f;
            }
        }
        else if (left && !right)
        {
            yaw -= 90.0f;
        }
        else if (right && !left)
        {
            yaw += 90.0f;
        }

        return Mth.wrapDegrees(yaw);
    }
}
