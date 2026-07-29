package net.shoreline.client.impl.render.rect;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import org.joml.Matrix3x2f;

public class RectBuilder implements GuiElementRenderState
{
    private RenderPipeline pipeline;
    private final Matrix3x2f matrix;
    private float startX, startY, endX, endY;
    private int color;
    private ScreenRectangle scissorStack;
    private ScreenRectangle bounds;
    private TextureSetup textureSetup;

    private float u1, v1, u2, v2;
    private boolean isTextured;

    private boolean isGradient;
    private boolean gradientSideways;
    private int gradientColor;

    public RectBuilder()
    {
        this.matrix = new Matrix3x2f();
        this.textureSetup = TextureSetup.noTexture();
        reset();
    }

    public RectBuilder reset()
    {
        this.startX = 0;
        this.startY = 0;
        this.endX = 0;
        this.endY = 0;
        this.color = 0;
        this.gradientColor = 0;
        this.u1 = 0;
        this.v1 = 0;
        this.u2 = 1;
        this.v2 = 1;
        this.isTextured = false;
        this.isGradient = false;
        this.gradientSideways = false;
        this.scissorStack = null;
        this.bounds = null;
        return this;
    }

    public RectBuilder setPipeline(RenderPipeline pipeline)
    {
        this.pipeline = pipeline;
        return this;
    }

    public RectBuilder setMatrix(Matrix3x2f matrix)
    {
        this.matrix.set(matrix);
        return this;
    }

    public RectBuilder setPosition(float startX, float startY, float endX, float endY)
    {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.bounds = null;
        return this;
    }

    public RectBuilder setColor(int color)
    {
        this.color = color;
        this.isGradient = false;
        return this;
    }

    public RectBuilder setGradient(int startColor, int endColor, boolean sideways)
    {
        this.color = startColor;
        this.gradientColor = endColor;

        this.gradientSideways = sideways;
        this.isGradient = true;
        return this;
    }

    public RectBuilder setTexture(TextureSetup textureSetup, float u1, float v1, float u2, float v2)
    {
        this.textureSetup = textureSetup;
        this.u1 = u1;
        this.v1 = v1;
        this.u2 = u2;
        this.v2 = v2;
        this.isTextured = true;
        return this;
    }

    public RectBuilder setTexture(TextureSetup textureSetup)
    {
        return setTexture(textureSetup, 0, 0, 1, 1);
    }

    public RectBuilder setScissor(ScreenRectangle scissorStack)
    {
        this.scissorStack = scissorStack;
        this.bounds = null;
        return this;
    }

    @Override
    public void buildVertices(VertexConsumer vertices)
    {
        if (isGradient)
        {
            setupGradientVertices(vertices);
        }
        else if (isTextured)
        {
            setupTexturedVertices(vertices);
        }
        else
        {
            setupSolidVertices(vertices);
        }
    }

    @Override
    public RenderPipeline pipeline()
    {
        return pipeline;
    }

    @Override
    public TextureSetup textureSetup()
    {
        return textureSetup;
    }

    @Override
    public ScreenRectangle scissorArea()
    {
        return scissorStack;
    }

    @Override
    public ScreenRectangle bounds()
    {
        if (bounds == null)
        {
            bounds = createBounds();
        }

        return bounds;
    }

    private void setupSolidVertices(VertexConsumer vertices)
    {
        vertices.addVertexWith2DPose(matrix, startX, startY).setColor(color);
        vertices.addVertexWith2DPose(matrix, startX, endY).setColor(color);
        vertices.addVertexWith2DPose(matrix, endX, endY).setColor(color);
        vertices.addVertexWith2DPose(matrix, endX, startY).setColor(color);
    }

    private void setupGradientVertices(VertexConsumer vertices)
    {
        if (gradientSideways)
        {
            vertices.addVertexWith2DPose(matrix, startX, startY).setColor(color);
            vertices.addVertexWith2DPose(matrix, startX, endY).setColor(color);
            vertices.addVertexWith2DPose(matrix, endX, endY).setColor(gradientColor);
            vertices.addVertexWith2DPose(matrix, endX, startY).setColor(gradientColor);
        }
        else
        {
            vertices.addVertexWith2DPose(matrix, endX, startY).setColor(color);
            vertices.addVertexWith2DPose(matrix, startX, startY).setColor(color);
            vertices.addVertexWith2DPose(matrix, startX, endY).setColor(gradientColor);
            vertices.addVertexWith2DPose(matrix, endX, endY).setColor(gradientColor);
        }
    }

    private void setupTexturedVertices(VertexConsumer vertices)
    {
        vertices.addVertexWith2DPose(matrix, startX, startY).setUv(u1, v1).setColor(color);
        vertices.addVertexWith2DPose(matrix, startX, endY).setUv(u1, v2).setColor(color);
        vertices.addVertexWith2DPose(matrix, endX, endY).setUv(u2, v2).setColor(color);
        vertices.addVertexWith2DPose(matrix, endX, startY).setUv(u2, v1).setColor(color);
    }

    private ScreenRectangle createBounds()
    {
        ScreenRectangle screenRect = new ScreenRectangle((int) startX, (int) startY, (int) (endX - startX), (int) (endY - startY)).transformAxisAligned(matrix);
        return scissorStack != null ? scissorStack.intersection(screenRect) : screenRect;
    }
}
