package net.shoreline.client.api.gui.handler;

import lombok.Getter;
import lombok.Setter;
import net.shoreline.client.api.interfaces.Globals;
import net.shoreline.client.util.input.Input;
import net.shoreline.client.util.math.Timer;
import org.lwjgl.glfw.GLFW;

@Getter
@Setter
public class TextHandler implements Globals
{
    private final Timer idleTimer = new Timer();
    private String text;
    private boolean idling;
    private String fallback;
    private boolean selected;

    public String getIdlingText()
    {
        return text + getIdleSign();
    }

    public void setText(String text)
    {
        this.text = text;
        this.fallback = text;
    }

    public void reset()
    {
        setText(fallback);
    }

    public void append(String text)
    {
        setText(getText() + text);
    }

    public void update(char chr)
    {
        if (selected)
        {
            setText("");
            selected = false;
        }

        setText(getText() + chr);
    }

    public void delete()
    {
        setText(removeLastChar(getText()));
    }

    public void onKey(int keyCode, int scanCode, int modifiers)
    {
        boolean control = Input.isKeyPressed(GLFW.GLFW_KEY_LEFT_CONTROL);
        switch (keyCode)
        {
            case GLFW.GLFW_KEY_A ->
            {
                if (control)
                {
                    setSelected(true);
                }
            }
            case GLFW.GLFW_KEY_C ->
            {
                if (isSelected() && control)
                {
                    mc.keyboardHandler.setClipboard(getText());
                }
            }
            case GLFW.GLFW_KEY_V ->
            {
                if (control)
                {
                    setText(mc.keyboardHandler.getClipboard());
                }
            }
            case GLFW.GLFW_KEY_ESCAPE ->
            {
                reset();
            }
            case 259 ->
            {
                delete();
                if (isSelected())
                {
                    setText("");
                    setSelected(false);
                }
            }
        }
    }

    public static String removeLastChar(String str)
    {
        String output = "";
        if (str != null && !str.isEmpty())
        {
            output = str.substring(0, str.length() - 1);
        }

        return output;
    }

    public String getIdleSign()
    {
        if (selected)
        {
            return "";
        }

        if (idleTimer.passed(500))
        {
            idling = !idling;
            idleTimer.reset();
        }

        if (idling)
        {
            return "_";
        }

        return "";
    }
}