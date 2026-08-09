package net.shoreline.client.api.gui.component;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.shoreline.client.api.gui.api.GuiComponent;
import net.shoreline.client.api.gui.api.Interactable;
import net.shoreline.client.api.setting.impl.EnumSetting;
import net.shoreline.client.impl.render.ColorUtil;
import net.shoreline.client.impl.render.Render2DUtil;
import net.shoreline.client.impl.render.animation.ColorAnimation;
import net.shoreline.client.impl.render.animation.Easing;
import net.shoreline.client.util.Formatter;

import java.awt.*;

public class EnumComponent<E extends Enum<E>> extends ParentComponent
{
    private final EnumSetting<E> setting;

    public EnumComponent(EnumSetting<E> setting)
    {
        super(setting.getName(), setting.getVisible());
        this.setting = setting;
        setup();
    }

    @Override
    public void drawComponent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks)
    {
        drawHoverRect(graphics);
        scissorText(graphics, Formatter.formatEnum(setting.getValue()));
        drawSettingText(graphics, this, getLabel(), false, false);
        graphics.disableScissor();

        String formatted = Formatter.formatEnum(setting.getValue());
        drawAnimatedRightText(graphics, Formatter.capitalize(formatted.toLowerCase()), false, partialTicks);

        if (animation.getFactor() > 0.001)
        {
            drawParentOutline(graphics);
        }
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button)
    {
        if (mouseWithinBounds(mouseX, mouseY, getAlignedX(), getY(), getWidth(), getFeatureHeight()))
        {
            int ordinal = setting.getValue().ordinal();
            E[] values = setting.getValue().getDeclaringClass().getEnumConstants();
            if (button == 0)
            {
                ordinal = (ordinal + 1) % values.length;
                setting.setValue(values[ordinal]);
            }
            else if (button == 1)
            {
                ordinal = (ordinal - 1 + values.length) % values.length;
                setting.setValue(values[ordinal]);
            }
            else if (button == 2)
            {
                setOpen(!open);
            }
        }

        if (open)
        {
            for (GuiComponent component : components)
            {
                if (component instanceof Interactable interactable && component.isVisible())
                {
                    interactable.mouseClicked(mouseX, mouseY, button);
                }
            }
        }
    }

    public void setup()
    {
        E[] values = setting.getValue().getDeclaringClass().getEnumConstants();
        for (E e : values)
        {
            components.add(new EnumValueComponent(e));
        }
    }

    private class EnumValueComponent extends RunnableComponent
    {
        private final ColorAnimation colorAnimation;
        private final E e;

        public EnumValueComponent(E e)
        {
            super(Formatter.capitalize(Formatter.formatEnum(e).toLowerCase()), () -> true, () -> setting.setValue(e));
            this.e = e;
            this.colorAnimation = new ColorAnimation(150, Easing.LINEAR);
        }

        @Override
        public void drawComponent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks)
        {
            double hFactor = hoverAnimation.getFactor();
            double eFactor = colorAnimation.getFactor();
            Color hoverColor = ColorUtil.withTransparency(
                    Color.GRAY,
                    Math.max(50, (int) (75 * hFactor))
            );

            Color clr = getTheme().getPrimaryC(0.5f);
            Color enabledColor = ColorUtil.interpolate(clr, clr.brighter(), hFactor);
            Color color = ColorUtil.interpolate(
                    hoverColor,
                    enabledColor,
                    eFactor
            );

            Render2DUtil.drawRect(graphics, getX(), getY() + 1.5f, getX() + getWidth(), getY() + getFeatureHeight(), color.getRGB());
            drawString(graphics, Formatter.formatEnum(e), (float) (getX() + getTextPadding() + getHoverAnimation().getCurrent()), getY() + (getFeatureHeight()) / 2 + 1.0f, 0xFFFFFFFF);
        }

        @Override
        public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks)
        {
            super.render(graphics, mouseX, mouseY, partialTicks);
            colorAnimation.setState(setting.getValue() == e);
        }
    }
}
