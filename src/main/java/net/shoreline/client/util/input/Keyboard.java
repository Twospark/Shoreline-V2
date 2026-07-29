package net.shoreline.client.util.input;

import lombok.experimental.UtilityClass;
import org.lwjgl.glfw.GLFW;

// thanks chatgpt
@UtilityClass
public class Keyboard
{
    public static String toString(int key)
    {
        return switch (key)
        {
            case GLFW.GLFW_KEY_0 -> "0";
            case GLFW.GLFW_KEY_1 -> "1";
            case GLFW.GLFW_KEY_2 -> "2";
            case GLFW.GLFW_KEY_3 -> "3";
            case GLFW.GLFW_KEY_4 -> "4";
            case GLFW.GLFW_KEY_5 -> "5";
            case GLFW.GLFW_KEY_6 -> "6";
            case GLFW.GLFW_KEY_7 -> "7";
            case GLFW.GLFW_KEY_8 -> "8";
            case GLFW.GLFW_KEY_9 -> "9";
            case GLFW.GLFW_KEY_A -> "A";
            case GLFW.GLFW_KEY_B -> "B";
            case GLFW.GLFW_KEY_C -> "C";
            case GLFW.GLFW_KEY_D -> "D";
            case GLFW.GLFW_KEY_E -> "E";
            case GLFW.GLFW_KEY_F -> "F";
            case GLFW.GLFW_KEY_G -> "G";
            case GLFW.GLFW_KEY_H -> "H";
            case GLFW.GLFW_KEY_I -> "I";
            case GLFW.GLFW_KEY_J -> "J";
            case GLFW.GLFW_KEY_K -> "K";
            case GLFW.GLFW_KEY_L -> "L";
            case GLFW.GLFW_KEY_M -> "M";
            case GLFW.GLFW_KEY_N -> "N";
            case GLFW.GLFW_KEY_O -> "O";
            case GLFW.GLFW_KEY_P -> "P";
            case GLFW.GLFW_KEY_Q -> "Q";
            case GLFW.GLFW_KEY_R -> "R";
            case GLFW.GLFW_KEY_S -> "S";
            case GLFW.GLFW_KEY_T -> "T";
            case GLFW.GLFW_KEY_U -> "U";
            case GLFW.GLFW_KEY_V -> "V";
            case GLFW.GLFW_KEY_W -> "W";
            case GLFW.GLFW_KEY_X -> "X";
            case GLFW.GLFW_KEY_Y -> "Y";
            case GLFW.GLFW_KEY_Z -> "Z";
            case GLFW.GLFW_KEY_F1 -> "F1";
            case GLFW.GLFW_KEY_F2 -> "F2";
            case GLFW.GLFW_KEY_F3 -> "F3";
            case GLFW.GLFW_KEY_F4 -> "F4";
            case GLFW.GLFW_KEY_F5 -> "F5";
            case GLFW.GLFW_KEY_F6 -> "F6";
            case GLFW.GLFW_KEY_F7 -> "F7";
            case GLFW.GLFW_KEY_F8 -> "F8";
            case GLFW.GLFW_KEY_F9 -> "F9";
            case GLFW.GLFW_KEY_F10 -> "F10";
            case GLFW.GLFW_KEY_F11 -> "F11";
            case GLFW.GLFW_KEY_F12 -> "F12";
            case GLFW.GLFW_KEY_RIGHT_SHIFT -> "RSHIFT";
            case GLFW.GLFW_KEY_LEFT_SHIFT -> "LSHIFT";
            case GLFW.GLFW_KEY_RIGHT_CONTROL -> "RCTRL";
            case GLFW.GLFW_KEY_LEFT_CONTROL -> "LCTRL";
            case GLFW.GLFW_KEY_LEFT_ALT -> "LEFT_ALT";
            case GLFW.GLFW_KEY_RIGHT_ALT -> "RIGHT_ALT";
            case GLFW.GLFW_KEY_BACKSLASH -> "BACKSLASH";
            case GLFW.GLFW_KEY_APOSTROPHE -> "APOSTROPHE";
            case GLFW.GLFW_KEY_BACKSPACE -> "BACKSPACE";
            case GLFW.GLFW_KEY_CAPS_LOCK -> "CAPS_LOCK";
            case GLFW.GLFW_KEY_COMMA -> "COMMA";
            case GLFW.GLFW_KEY_PERIOD -> "PERIOD";
            case GLFW.GLFW_KEY_SLASH -> "SLASH";
            case GLFW.GLFW_KEY_ENTER -> "ENTER";
            case GLFW.GLFW_KEY_EQUAL -> "EQUAL";
            case GLFW.GLFW_KEY_ESCAPE -> "ESCAPE";
            case GLFW.GLFW_KEY_KP_ADD -> "+";
            case GLFW.GLFW_KEY_KP_SUBTRACT -> "-";
            case GLFW.GLFW_KEY_KP_DIVIDE -> ":";
            case GLFW.GLFW_KEY_KP_MULTIPLY -> "*";
            case GLFW.GLFW_KEY_GRAVE_ACCENT -> "~";

            // Mouse Buttons
            case GLFW.GLFW_KEY_LAST + 1 -> "MOUSE0"; // GLFW_MOUSE_BUTTON_1
            case GLFW.GLFW_KEY_LAST + 2 -> "MOUSE1"; // GLFW_MOUSE_BUTTON_2
            case GLFW.GLFW_KEY_LAST + 3 -> "MOUSE2"; // GLFW_MOUSE_BUTTON_3
            case GLFW.GLFW_KEY_LAST + 4 -> "MOUSE3"; // GLFW_MOUSE_BUTTON_4
            case GLFW.GLFW_KEY_LAST + 5 -> "MOUSE4"; // GLFW_MOUSE_BUTTON_5
            case GLFW.GLFW_KEY_LAST + 6 -> "MOUSE5"; // GLFW_MOUSE_BUTTON_6
            case GLFW.GLFW_KEY_LAST + 7 -> "MOUSE6"; // GLFW_MOUSE_BUTTON_7
            case GLFW.GLFW_KEY_LAST + 8 -> "MOUSE7"; // GLFW_MOUSE_BUTTON_8
            default -> "NONE";
        };
    }

    public static int fromString(String stringIn)
    {
        String string = stringIn.toUpperCase();
        return switch (string)
        {
            case "0" -> GLFW.GLFW_KEY_0;
            case "1" -> GLFW.GLFW_KEY_1;
            case "2" -> GLFW.GLFW_KEY_2;
            case "3" -> GLFW.GLFW_KEY_3;
            case "4" -> GLFW.GLFW_KEY_4;
            case "5" -> GLFW.GLFW_KEY_5;
            case "6" -> GLFW.GLFW_KEY_6;
            case "7" -> GLFW.GLFW_KEY_7;
            case "8" -> GLFW.GLFW_KEY_8;
            case "9" -> GLFW.GLFW_KEY_9;
            case "A" -> GLFW.GLFW_KEY_A;
            case "B" -> GLFW.GLFW_KEY_B;
            case "C" -> GLFW.GLFW_KEY_C;
            case "D" -> GLFW.GLFW_KEY_D;
            case "E" -> GLFW.GLFW_KEY_E;
            case "F" -> GLFW.GLFW_KEY_F;
            case "G" -> GLFW.GLFW_KEY_G;
            case "H" -> GLFW.GLFW_KEY_H;
            case "I" -> GLFW.GLFW_KEY_I;
            case "J" -> GLFW.GLFW_KEY_J;
            case "K" -> GLFW.GLFW_KEY_K;
            case "L" -> GLFW.GLFW_KEY_L;
            case "M" -> GLFW.GLFW_KEY_M;
            case "N" -> GLFW.GLFW_KEY_N;
            case "O" -> GLFW.GLFW_KEY_O;
            case "P" -> GLFW.GLFW_KEY_P;
            case "Q" -> GLFW.GLFW_KEY_Q;
            case "R" -> GLFW.GLFW_KEY_R;
            case "S" -> GLFW.GLFW_KEY_S;
            case "T" -> GLFW.GLFW_KEY_T;
            case "U" -> GLFW.GLFW_KEY_U;
            case "V" -> GLFW.GLFW_KEY_V;
            case "W" -> GLFW.GLFW_KEY_W;
            case "X" -> GLFW.GLFW_KEY_X;
            case "Y" -> GLFW.GLFW_KEY_Y;
            case "Z" -> GLFW.GLFW_KEY_Z;
            case "F1" -> GLFW.GLFW_KEY_F1;
            case "F2" -> GLFW.GLFW_KEY_F2;
            case "F3" -> GLFW.GLFW_KEY_F3;
            case "F4" -> GLFW.GLFW_KEY_F4;
            case "F5" -> GLFW.GLFW_KEY_F5;
            case "F6" -> GLFW.GLFW_KEY_F6;
            case "F7" -> GLFW.GLFW_KEY_F7;
            case "F8" -> GLFW.GLFW_KEY_F8;
            case "F9" -> GLFW.GLFW_KEY_F9;
            case "F10" -> GLFW.GLFW_KEY_F10;
            case "F11" -> GLFW.GLFW_KEY_F11;
            case "F12" -> GLFW.GLFW_KEY_F12;
            case "RSHIFT", "RIGHT_SHIFT", "R_SHIFT" -> GLFW.GLFW_KEY_RIGHT_SHIFT;
            case "SHIFT", "LSHIFT", "LEFT_SHIFT", "L_SHIFT" -> GLFW.GLFW_KEY_LEFT_SHIFT;
            case "RCTRL", "RIGHT_CTRL", "R_CTRL" -> GLFW.GLFW_KEY_RIGHT_CONTROL;
            case "CTRL", "LCTRL", "LEFT_CTRL", "L_CTRL" -> GLFW.GLFW_KEY_LEFT_CONTROL;
            case "LEFT_ALT", "L_ALT" -> GLFW.GLFW_KEY_LEFT_ALT;
            case "RIGHT_ALT", "R_ALT" -> GLFW.GLFW_KEY_RIGHT_ALT;
            case "BACKSLASH" -> GLFW.GLFW_KEY_BACKSLASH;
            case "APOSTROPHE" -> GLFW.GLFW_KEY_APOSTROPHE;
            case "BACKSPACE" -> GLFW.GLFW_KEY_BACKSPACE;
            case "CAPS_LOCK" -> GLFW.GLFW_KEY_CAPS_LOCK;
            case "COMMA" -> GLFW.GLFW_KEY_COMMA;
            case "PERIOD" -> GLFW.GLFW_KEY_PERIOD;
            case "SLASH" -> GLFW.GLFW_KEY_SLASH;
            case "ENTER" -> GLFW.GLFW_KEY_ENTER;
            case "EQUAL", "=" -> GLFW.GLFW_KEY_EQUAL;
            case "ESCAPE" -> GLFW.GLFW_KEY_ESCAPE;
            case "+" -> GLFW.GLFW_KEY_KP_ADD;
            case "-" -> GLFW.GLFW_KEY_KP_SUBTRACT;
            case ":" -> GLFW.GLFW_KEY_KP_DIVIDE;
            case "*" -> GLFW.GLFW_KEY_KP_MULTIPLY;
            case "~" -> GLFW.GLFW_KEY_GRAVE_ACCENT;
            case "MOUSE0" -> GLFW.GLFW_KEY_LAST + 1;
            case "MOUSE1" -> GLFW.GLFW_KEY_LAST + 2;
            case "MOUSE2" -> GLFW.GLFW_KEY_LAST + 3;
            case "MOUSE3" -> GLFW.GLFW_KEY_LAST + 4;
            case "MOUSE4" -> GLFW.GLFW_KEY_LAST + 5;
            case "MOUSE5" -> GLFW.GLFW_KEY_LAST + 6;
            case "MOUSE6" -> GLFW.GLFW_KEY_LAST + 7;
            case "MOUSE7" -> GLFW.GLFW_KEY_LAST + 8;
            default -> GLFW.GLFW_KEY_UNKNOWN;
        };
    }
}