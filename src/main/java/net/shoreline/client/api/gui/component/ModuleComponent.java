package net.shoreline.client.api.gui.component;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.shoreline.client.api.module.Module;
import net.shoreline.client.api.module.Toggleable;
import net.shoreline.client.impl.render.ColorUtil;
import net.shoreline.client.impl.render.Render2DUtil;
import net.shoreline.client.impl.render.animation.ColorAnimation;
import net.shoreline.client.impl.render.animation.Easing;

import java.awt.*;

public class ModuleComponent extends GridParentComponent
{
    private final Module module;
    private final ColorAnimation colorAnimation;

    public ModuleComponent(Module module)
    {
        super(module.getName(), () -> true, 1);
        this.module = module;
        this.colorAnimation = new ColorAnimation(150, Easing.LINEAR);

        if (module instanceof Toggleable toggleable)
        {
            colorAnimation.setStateHard(toggleable.isEnabled());
        }
        else
        {
            colorAnimation.setStateHard(true);
        }
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

        if (animation.getFactor() > 0.001)
        {
            float outlineY = getY() + getFeatureHeight();
            float outlineHeight = getHeight() - getFeatureHeight() + 0.5f;
            Render2DUtil.drawRect(graphics, getX(), outlineY, getX() + 1.0f, outlineY + outlineHeight, enabledColor.getRGB());
        }

        Render2DUtil.drawRect(graphics, getX(), getY() + 1.5f, getX() + getWidth(), getY() + getFeatureHeight(), color.getRGB());
        drawString(graphics, getLabel(), getX() + 3f, getY() + (getFeatureHeight()) / 2 + 1, 0xFFFFFFFF);
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks)
    {
        super.render(graphics, mouseX, mouseY, partialTicks);
        if (module instanceof Toggleable toggleable)
        {
            colorAnimation.setState(toggleable.isEnabled());
        }
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button)
    {
        super.mouseClicked(mouseX, mouseY, button);
        if (module instanceof Toggleable toggleable
                && mouseWithinBounds(mouseX, mouseY, getX(), getY(), getWidth(), getFeatureHeight())
                && button == 0)
        {
            toggleable.toggle();
        }
    }

    @Override
    public boolean modifyParentPadding()
    {
        return false;
    }

    @Override
    public float getRightPadding()
    {
        return 1f;
    }
}
