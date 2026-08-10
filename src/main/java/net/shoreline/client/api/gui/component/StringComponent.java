package net.shoreline.client.api.gui.component;

import lombok.Getter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.shoreline.client.api.gui.ShorelineGui;
import net.shoreline.client.api.gui.api.Interactable;
import net.shoreline.client.api.gui.handler.TextHandler;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Getter
public class StringComponent extends AbstractComponent implements Interactable
{
    private final TextHandler handler = new TextHandler();
    private final Supplier<String> supplier;
    private final Consumer<String> consumer;

    private boolean typing;

    public StringComponent(String label, Supplier<Boolean> visibility, Supplier<String> supplier, Consumer<String> consumer)
    {
        super(label, visibility);
        this.supplier = supplier;
        this.consumer = consumer;
        handler.setText(supplier.get());
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks)
    {
        super.render(graphics, mouseX, mouseY, partialTicks);
        setHeight(getFeatureHeight());
    }

    @Override
    public void drawComponent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks)
    {
        String value = typing ? handler.getIdlingText() : supplier.get();
        drawValueComponent(graphics, value, partialTicks);
    }

    // maybe add cursor one day
    @Override
    public void mouseClicked(double mouseX, double mouseY, int button)
    {
        if (isHovered(mouseX, mouseY))
        {
            typing = !typing;
            consumer.accept(handler.getText());
        }
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {}

    @Override
    public void mouseScrolled(double x, double y, double scrollX, double scrollY) {}

    @Override
    public void keyTyped(int key, int scancode, int modifiers)
    {
        if (typing)
        {
            handler.onKey(key, scancode, modifiers);
        }

        if (key == GLFW.GLFW_KEY_ESCAPE || key == GLFW.GLFW_KEY_ENTER)
        {
            ShorelineGui.cancelEscape = true;
            consumer.accept(handler.getText());
            typing = false;
        }
    }

    @Override
    public void charTyped(char chr)
    {
        if (typing)
        {
            handler.update(chr);
        }
    }
}
