package net.shoreline.client.api.gui.component;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.shoreline.client.api.setting.impl.ToggleableSettingGroup;
import net.shoreline.client.impl.render.ColorUtil;
import net.shoreline.client.impl.render.Render2DUtil;
import net.shoreline.client.impl.render.animation.ColorAnimation;
import net.shoreline.client.impl.render.animation.Easing;

import java.awt.*;

public class ToggleableGroupComponent extends ParentComponent
{
    private final ToggleableSettingGroup setting;
    private final ColorAnimation colorAnimation;

    public ToggleableGroupComponent(ToggleableSettingGroup setting)
    {
        super(setting.getName(), setting.getVisible());
        this.setting = setting;
        this.colorAnimation = new ColorAnimation(150, Easing.LINEAR);
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks)
    {
        super.render(graphics, mouseX, mouseY, partialTicks);
        colorAnimation.setState(setting.getValue());
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

        String value = open ? "-" : "+";
        Render2DUtil.drawRect(graphics, getX(), getY() + 1.5f, getX() + getWidth(), getY() + getFeatureHeight(), color.getRGB());
        scissorText(graphics, value);
        drawSettingText(graphics, this, getLabel(), false, false);
        graphics.disableScissor();
        drawAnimatedRightText(graphics, value, false, partialTicks);
        if (animation.getFactor() > 0.001)
        {
            drawParentOutline(graphics);
        }
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button)
    {
        super.mouseClicked(mouseX, mouseY, button);
        if (mouseWithinBounds(mouseX, mouseY, getX(), getY(), getWidth(), getFeatureHeight()) && button == 0)
        {
            setting.setValue(!setting.getValue());
        }
    }
}
