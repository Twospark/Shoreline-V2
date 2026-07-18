package net.shoreline.client.impl.render;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.shoreline.client.impl.render.rect.RectBuilder;
import org.joml.Matrix3x2f;

public class Render2DUtil
{
    public static void drawRect(GuiGraphicsExtractor graphics, float startX, float startY, float endX, float endY, int color)
    {
        RectBuilder rect = new RectBuilder()
                .setPipeline(RenderPipelines.GUI)
                .setPosition(startX, startY, endX, endY)
                .setColor(color)
                .setMatrix(new Matrix3x2f(graphics.pose()))
                .setScissor(graphics.scissorStack.peek());

        graphics.guiRenderState.addGuiElement(rect);
    }

    public static void drawBorderedRect(GuiGraphicsExtractor graphics, float startX, float startY, float endX, float endY, float lineSize,  int color, int borderColor)
    {
        drawRect(graphics, startX, startY, endX, endY, color);
        drawRect(graphics, startX, startY, startX + lineSize, endY, borderColor);
        drawRect(graphics, endX - lineSize, startY, endX, endY, borderColor);
        drawRect(graphics, startX, endY - lineSize, endX, endY, borderColor);
        drawRect(graphics, startX, startY, endX, startY + lineSize, borderColor);
    }
}