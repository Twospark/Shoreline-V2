package net.shoreline.client.api.gui.component;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.shoreline.client.api.gui.api.Interactable;
import net.shoreline.client.api.setting.impl.BooleanSetting;
import net.shoreline.client.impl.render.ColorUtil;
import net.shoreline.client.impl.render.Render2DUtil;
import net.shoreline.client.impl.render.animation.ColorAnimation;
import net.shoreline.client.impl.render.animation.Easing;

import java.awt.*;

public class BooleanComponent extends AbstractComponent implements Interactable
{
    private final ColorAnimation colorAnimation;
    private final BooleanSetting setting;

    public BooleanComponent(BooleanSetting setting)
    {
        super(setting.getName(), setting.getVisible());
        this.setting = setting;
        this.colorAnimation = new ColorAnimation(150, Easing.LINEAR);
        this.colorAnimation.setState(setting.getValue());
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
        drawString(graphics, getLabel(), (float) (getX() + getTextPadding() + getHoverAnimation().getCurrent()), getY() + (getFeatureHeight()) / 2 + 1f, 0xFFFFFFFF);
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks)
    {
        super.render(graphics, mouseX, mouseY, partialTicks);
        setHeight(getFeatureHeight());
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button)
    {
        if (isHovered(mouseX, mouseY) && button == 0)
        {
            setting.setValue(!setting.getValue());
            colorAnimation.setState(setting.getValue());
        }
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button)
    {

    }

    @Override
    public void mouseScrolled(double x, double y, double scrollX, double scrollY)
    {

    }

    @Override
    public void keyTyped(int key, int scancode, int modifiers)
    {

    }

    @Override
    public void charTyped(char chr)
    {

    }
}
