package net.shoreline.client.impl.render;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.Identifier;
import net.shoreline.client.api.interfaces.Globals;
import net.shoreline.client.impl.render.rect.RectBuilder;
import org.joml.Matrix3x2f;

public class Render2DUtil implements Globals
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

    public static void drawGradientRect(GuiGraphicsExtractor graphics,
                                        float startX,
                                        float startY,
                                        float endX,
                                        float endY,
                                        boolean sideways,
                                        int startColor,
                                        int endColor)
    {
        RectBuilder rect = new RectBuilder();
        rect.setPipeline(RenderPipelines.GUI)
                .setMatrix(new Matrix3x2f(graphics.pose()))
                .setPosition(startX, startY, endX, endY)
                .setGradient(startColor, endColor, sideways)
                .setScissor(graphics.scissorStack.peek());

        graphics.guiRenderState.addGuiElement(rect);
    }

    public static void drawTexture(GuiGraphicsExtractor graphics, Identifier identifier, float startX, float startY, float endX, float endY, int color)
    {
        AbstractTexture texture = mc.getTextureManager().getTexture(identifier);
        RectBuilder rect = new RectBuilder();
        rect.setMatrix(new Matrix3x2f(new Matrix3x2f(graphics.pose())))
                .setTexture(TextureSetup.singleTexture(texture.getTextureView(), texture.getSampler()))
                .setPosition(startX, startY, startX + endX, startY + endY)
                .setColor(color)
                .setPipeline(RenderPipelines.GUI_TEXTURED)
                .setScissor(graphics.scissorStack.peek());

        graphics.guiRenderState.addGuiElement(rect);
    }

    public static void drawTexture(GuiGraphicsExtractor graphics, Identifier identifier, float startX, float startY, float endX, float endY, float u, float v, int color)
    {
        AbstractTexture texture = mc.getTextureManager().getTexture(identifier);
        RectBuilder rect = new RectBuilder();
        rect.setMatrix(new Matrix3x2f(new Matrix3x2f(graphics.pose())))
                .setTexture(TextureSetup.singleTexture(texture.getTextureView(), texture.getSampler()), 0, 0, u, v)
                .setPosition(startX, startY, startX + endX, startY + endY)
                .setColor(color)
                .setPipeline(RenderPipelines.GUI_TEXTURED)
                .setScissor(graphics.scissorStack.peek());

        graphics.guiRenderState.addGuiElement(rect);
    }
}