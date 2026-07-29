package net.shoreline.client.api.gui.handler;

import com.mojang.blaze3d.platform.Window;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.shoreline.client.api.gui.component.WindowComponent;
import net.shoreline.client.api.interfaces.Globals;
import net.shoreline.client.impl.render.animation.Smoother;

@RequiredArgsConstructor
@Getter
public class ScrollHandler implements Globals
{
    private final WindowComponent component;
    private final Smoother smoother = new Smoother();
    private final float padding;
    private float scroll;
    private float currentScroll;

    public void handleRender(float mouseX, float mouseY, float partialTicks)
    {
        if (!component.mouseWithinBounds(mouseX, mouseY,
                component.getX(),
                component.getY(),
                component.getWidth(),
                component.getHeight()))
        {
            currentScroll = 0;
        }

        Window window = mc.getWindow();
        float outOfBounds  = component.getY() + component.getMaxHeight() - window.getGuiScaledHeight();
        float windowHeight = window.getGuiScaledHeight() - padding;
        float cHeight      = component.getHeight();
        float maxHeight    = Math.min(cHeight, windowHeight) - Math.max(0, outOfBounds);
        float maxScroll    = Math.max(0, cHeight - maxHeight);

        scroll -= (float) smoother.smooth(currentScroll, 0.5, partialTicks);

        /* Prevents overscroll, but smoothly! */
        float overscroll = Math.min(0, scroll) + Math.max(0, scroll - maxScroll);
        scroll -= overscroll * 0.2f;
        currentScroll = 0;
    }

    public void mouseScrolled(double scrollY)
    {
        currentScroll = (float) (scrollY * 20);
    }
}