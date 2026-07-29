package net.shoreline.client.api.gui.component;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.shoreline.client.api.gui.api.Interactable;
import net.shoreline.client.api.gui.handler.NumberHandler;
import net.shoreline.client.api.gui.handler.TextHandler;
import net.shoreline.client.impl.render.Render2DUtil;
import net.shoreline.client.impl.render.animation.Smoother;
import org.lwjgl.glfw.GLFW;

import java.util.function.Supplier;

public class NumberComponent extends AbstractComponent implements Interactable
{
    private final TextHandler textHandler = new TextHandler();
    private final Smoother filter = new Smoother();
    private final NumberHandler numberHandler;
    private boolean dragging;
    private boolean listening;

    public NumberComponent(NumberHandler handler)
    {
        this("Component", () -> true, handler);
    }

    public NumberComponent(String label, Supplier<Boolean> visibility, NumberHandler handler)
    {
        super(label, visibility);
        this.numberHandler = handler;
    }

    public void drawSlider(GuiGraphicsExtractor graphics, String text, float fill, float partialTicks)
    {
        float sliderWidth = getWidth() * fill;
        Render2DUtil.drawRect(graphics, getX(), getY() + 1.5f, getX() + sliderWidth, getY() + getFeatureHeight() - 0.5f, getTheme().getPrimary(0.5f));
        drawHoverRect(graphics);
        scissorText(graphics, text);
        drawSettingText(graphics, this, getLabel(), false, false);
        graphics.disableScissor();
        drawAnimatedRightText(graphics, text, false, partialTicks);
    }

    @Override
    public void drawComponent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks)
    {
        String text = listening
                ? textHandler.getIdlingText()
                : numberHandler.getValue().toString();

        float percent = numberHandler.getPercent();
        float smoother = (float) filter.smooth(percent, 0.5, partialTicks);
        drawSlider(graphics, text, smoother, partialTicks);
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks)
    {
        super.render(graphics, mouseX, mouseY, partialTicks);
        setHeight(getFeatureHeight());
        if (dragging)
        {
            float percent = (mouseX - getX()) / getWidth();
            numberHandler.update(percent);
        }
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button)
    {
        if (isHovered(mouseX, mouseY))
        {
            if (button == 0)
            {
                dragging = true;
                if (listening)
                {
                    listening = false;
                    textHandler.reset();
                }
            }
            else if (button == 1)
            {
                if (listening)
                {
                    textHandler.reset();
                    listening = false;
                    return;
                }

                listening = true;
                textHandler.setText("");
            }
        }
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
    public void keyTyped(int key, int scancode, int modifiers)
    {
        if (listening)
        {
            if (key == GLFW.GLFW_KEY_BACKSPACE)
            {
                if (textHandler.getText().isEmpty())
                {
                    listening = false;
                    return;
                }

                textHandler.delete();
                return;
            }

            if (key == GLFW.GLFW_KEY_ESCAPE)
            {
                textHandler.reset();
                listening = false;
            }

            if (key == GLFW.GLFW_KEY_ENTER)
            {
                numberHandler.updateText(textHandler.getText());
                listening = false;
            }
        }
    }

    @Override
    public void charTyped(char chr)
    {
        if (listening)
        {
            textHandler.update(chr);
        }
    }
}
