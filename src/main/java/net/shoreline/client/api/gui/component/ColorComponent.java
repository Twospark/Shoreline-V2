package net.shoreline.client.api.gui.component;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.shoreline.client.api.gui.api.Interactable;
import net.shoreline.client.api.gui.handler.NumberHandler;
import net.shoreline.client.api.setting.impl.ColorSetting;
import net.shoreline.client.impl.render.ColorUtil;
import net.shoreline.client.impl.render.Render2DUtil;
import net.shoreline.client.impl.render.animation.Smoother;

import java.awt.*;

public class ColorComponent extends ParentComponent
{
    private static final Identifier HUE   =
        Identifier.fromNamespaceAndPath(
            "shoreline", "textures/gui/rainbow.png");
    private static final Identifier ALPHA =
        Identifier.fromNamespaceAndPath(
            "shoreline", "textures/gui/transparent.png");

    private final ColorSetting setting;
    private final float[] hsb;

    public ColorComponent(ColorSetting color)
    {
        super(color.getName(), color.getVisible());
        this.setting = color;
        this.hsb = color.getHsb().clone();
        setup();
    }

    @Override
    public void drawComponent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks)
    {
        drawHoverRect(graphics);
        drawSettingText(graphics, this, getLabel(), false, false);
        Render2DUtil.drawBorderedRect(graphics, getAlignedX() + getWidth() - 11, getY() + 4, getAlignedX() + getWidth() - 2, getY() + getFeatureHeight() - 2, 0.5f, setting.getValue().getRGB(), getTheme().getPrimary());

        if (animation.getFactor() > 0.001)
        {
            drawParentOutline(graphics);
        }
    }

    public void setup()
    {
        // ._.
        NumberHandler<Float> brightness = new NumberHandler<>
                (() -> get(2), (value) -> set(2, value), v -> (float) v);
        NumberHandler<Float> saturation = new NumberHandler<>
                (() -> get(1), (value) -> set(1, value), v -> (float) v);
        NumberHandler<Float> hue        = new NumberHandler<>
                (() -> get(0), (value) -> set(0, value), v -> (float) v);
        NumberHandler<Float> alpha      = new NumberHandler<>
                (this::getAlpha, this::setAlpha, v -> (float) v);

        components.add(new PickerComponent(brightness, saturation));
        components.add(new NumberComponent(hue)
        {
            @Override
            public float getHeight()
            {
                return 9f;
            }

            @Override
            public void drawSlider(GuiGraphicsExtractor graphics, String value, float fill, float partialTicks)
            {
                float sliderX = getX();
                float sliderY = getY() + 1.0f;
                float sliderW = getWidth() - 2;
                float sliderH = getHeight() - 2f;

                float pos = (sliderW - 1) * fill;
                float pickerX = sliderX + pos;
                float pickerW = 1f;

                Render2DUtil.drawTexture(graphics, HUE, sliderX, sliderY, sliderW, sliderH, 0xFFFFFFFF);
                Render2DUtil.drawRect(graphics, pickerX - 0.5f, sliderY - 0.5f, pickerX + pickerW + 0.5f, sliderY + sliderH + 0.5f, 0xFF000000);
                Render2DUtil.drawRect(graphics, pickerX, sliderY, pickerX + pickerW, sliderY + sliderH, 0xFFFFFFFF);
            }
        });

        if (setting.isTransparency())
        {
            components.add(new NumberComponent(alpha)
            {
                @Override
                public float getHeight()
                {
                    return 9f;
                }

                @Override
                public void drawSlider(GuiGraphicsExtractor graphics, String value, float fill, float partialTicks)
                {
                    float sliderX = getX();
                    float sliderY = getY() + 1.0f;
                    float sliderW = getWidth() - 2;
                    float sliderH = getHeight() - 1.0f;

                    float pos = (sliderW - 1) * fill;
                    float pickerX = sliderX + pos;
                    float pickerW = 1f;
                    Color clr = new Color(setting.getRed(), setting.getGreen(), setting.getBlue());
                    Render2DUtil.drawTexture(graphics, ALPHA, sliderX, sliderY, sliderW, sliderH, 9f, 9f, 0xFFFFFFFF);
                    Render2DUtil.drawGradientRect(graphics, sliderX, sliderY, sliderX + sliderW, sliderY + sliderH, true, 0, clr.getRGB());
                    Render2DUtil.drawRect(graphics, pickerX - 0.5f, sliderY - 0.5f, pickerX + pickerW + 0.5f, sliderY + sliderH + 0.5f, 0xFF000000);
                    Render2DUtil.drawRect(graphics, pickerX, sliderY, pickerX + pickerW, sliderY + sliderH, 0xFFFFFFFF);
                }
            });
        }
    }

    public float get(int index)
    {
        return hsb[index];
    }

    public void set(int index, float value)
    {
        hsb[index] = value;
        Color hsbColor = new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
        setting.setValue(ColorUtil.withTransparency(hsbColor, setting.getAlpha()));
    }

    public float getAlpha()
    {
        return setting.getAlpha() / 255f;
    }

    public void setAlpha(float alpha)
    {
        setting.setAlpha(alpha);
    }

    private class PickerComponent extends AbstractComponent implements Interactable
    {
        private final Smoother bFilter = new Smoother();
        private final Smoother sFilter = new Smoother();
        private final NumberHandler<Float> brightness;
        private final NumberHandler<Float> saturation;
        private boolean dragging = false;

        public PickerComponent(NumberHandler<Float> brightness, NumberHandler<Float> saturation)
        {
            super("Component", () -> true);
            this.brightness = brightness;
            this.saturation = saturation;
        }

        public void drawSlider(GuiGraphicsExtractor graphics, float brightness, float saturation)
        {
            float pickerX = getX();
            float pickerY = getY() - 0.5f;
            float pickerWidth = getWidth();
            float pickerHeight = getHeight() + 1.0f;
            Render2DUtil.drawBorderedRect(graphics, pickerX, pickerY, pickerX + pickerWidth, pickerY + pickerHeight, 0.5f, 0, 0xFF000000);

            float hue = get(0);
            int hueColor = Color.HSBtoRGB(hue, 1.0f, 1.0f);
            Render2DUtil.drawGradientRect(graphics, x, y, x + width, y + height, true, 0xFFFFFFFF, hueColor);
            Render2DUtil.drawGradientRect(graphics, x, y, x + width, y + height, false, 0, 0xFF000000);

            float pointX = pickerX + saturation * pickerWidth;
            float pointY = pickerY + pickerHeight - brightness * pickerHeight;

            Render2DUtil.drawRect(graphics, pointX - 1.5f, pointY - 1.5f, pointX + 1.5f, pointY + 1.5f, 0xFF000000);
            Render2DUtil.drawRect(graphics, pointX - 1.0f, pointY - 1.0f, pointX + 1.0f, pointY + 1.0f, 0xFFFFFFFF);
        }

        @Override
        public float getWidth()
        {
            return super.getWidth() - 2f;
        }

        @Override
        public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks)
        {
            super.render(graphics, mouseX, mouseY, partialTicks);
            setHeight(getWidth());
            setY(getY() + getPadding());
            if (dragging)
            {
                float b = (getY() + getHeight() - mouseY) / getHeight();
                float s = (mouseX - getX()) / getWidth();
                brightness.update(b);
                saturation.update(s);
            }
        }

        @Override
        public void drawComponent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks)
        {
            float sPercent = saturation.getPercent();
            float bPercent = brightness.getPercent();
            float saturation = (float) sFilter.smooth(sPercent, 0.5f, partialTicks);
            float brightness = (float) bFilter.smooth(bPercent, 0.5f, partialTicks);
            drawSlider(graphics, brightness, saturation);
        }

        @Override
        public void mouseClicked(double mouseX, double mouseY, int button)
        {
            boolean hovered = mouseWithinBounds(mouseX,
                    mouseY,
                    getAlignedX(),
                    getY(),
                    getWidth(),
                    getHeight() - 4);

            dragging = hovered && button == 0;
        }

        @Override
        public void mouseReleased(double mouseX, double mouseY, int button)
        {
            dragging = false;
        }

        @Override
        public void mouseScrolled(double x, double y, double scrollX, double scrollY)
        {

        }

        @Override
        public void keyTyped(int keyCode, int scanCode, int modifiers)
        {

        }

        @Override
        public void charTyped(char typedChar)
        {

        }
    }
}