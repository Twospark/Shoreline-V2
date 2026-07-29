package net.shoreline.client.util.input;

import lombok.experimental.UtilityClass;
import org.lwjgl.glfw.GLFW;

@UtilityClass
public class Input
{
    private final boolean[] keys = new boolean[GLFW.GLFW_KEY_LAST];

    public static void setKeyState(int key, boolean pressed)
    {
        if (key >= 0 && key < keys.length)
        {
            keys[key] = pressed;
        }
    }

    public static boolean isKeyPressed(int key)
    {
        if (key == -1)
        {
            return false;
        }

        return keys[key];
    }
}
