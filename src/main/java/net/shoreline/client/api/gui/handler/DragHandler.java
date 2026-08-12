package net.shoreline.client.api.gui.handler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.function.Consumer;
import java.util.function.Supplier;

@RequiredArgsConstructor
@Getter
@Setter
public class DragHandler
{
    protected final Supplier<Float> xSupplier;
    protected final Supplier<Float> ySupplier;
    protected final Consumer<Float> xSetter;
    protected final Consumer<Float> ySetter;
    protected float clickedX;
    protected float clickedY;
    protected boolean dragging;

    public void handleRender(float mouseX, float mouseY)
    {
        if (dragging)
        {
            float resultX = mouseX + clickedX;
            float resultY = mouseY + clickedY;
            xSetter.accept(resultX);
            ySetter.accept(resultY);
        }
    }

    public void handleMouseClicked(float mouseX, float mouseY, int button, boolean hovered)
    {
        dragging = false;
        if (hovered && button == 0)
        {
            dragging = true;
            clickedX = xSupplier.get() - mouseX;
            clickedY = ySupplier.get() - mouseY;
        }
    }
}