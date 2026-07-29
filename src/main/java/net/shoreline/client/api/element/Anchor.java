package net.shoreline.client.api.element;

import net.shoreline.client.api.interfaces.Globals;

public enum Anchor implements Globals
{
    NONE
    {
        @Override
        public float getX(float screenWidth, float elementWidth)
        {
            return 0;
        }

        @Override
        public float getY(float screenHeight, float elementHeight, float offset)
        {
            return offset;
        }
    },
    TOP_LEFT
    {
        @Override
        public float getX(float screenWidth, float elementWidth)
        {
            return 0;
        }

        @Override
        public float getY(float screenHeight, float elementHeight, float offset)
        {
            return offset;
        }
    },
    TOP_RIGHT
    {
        @Override
        public float getX(float screenWidth, float elementWidth)
        {
            return screenWidth - elementWidth;
        }

        @Override
        public float getY(float screenHeight, float elementHeight, float offset)
        {
            if (!mc.player.getActiveEffects().isEmpty())
            {
                return offset + 25;
            }

            return offset;
        }
    },
    BOTTOM_LEFT
    {
        @Override
        public float getX(float screenWidth, float elementWidth)
        {
            return 0;
        }

        @Override
        public float getY(float screenHeight, float elementHeight, float offset)
        {
            if (mc.gui.getChat().isChatFocused())
            {
                return screenHeight - elementHeight - offset - 15.0f;
            }

            return screenHeight - elementHeight - offset;
        }
    },
    BOTTOM_RIGHT
    {
        @Override
        public float getX(float screenWidth, float elementWidth)
        {
            return screenWidth - elementWidth;
        }

        @Override
        public float getY(float screenHeight, float elementHeight, float offset)
        {
            if (mc.gui.getChat().isChatFocused())
            {
                return screenHeight - elementHeight - offset - 15.0f;
            }

            return screenHeight - elementHeight - offset;
        }
    },
    MIDDLE
    {
        @Override
        public float getX(float screenWidth, float elementWidth)
        {
            return (screenWidth / 2f) - (elementWidth / 2f);
        }

        @Override
        public float getY(float screenHeight, float elementHeight, float offset)
        {
            return offset;
        }
    };

    public abstract float getX(float screenWidth, float elementWidth);

    public abstract float getY(float screenHeight, float elementHeight, float offset);
}