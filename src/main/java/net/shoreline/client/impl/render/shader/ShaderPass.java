package net.shoreline.client.impl.render.shader;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.resource.CrossFrameResourcePool;
import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Getter;
import net.shoreline.client.api.common.Feature;

@Getter
public class ShaderPass extends Feature
{
    private final RenderTarget target;
    private final RenderTarget output;
    private final CrossFrameResourcePool pool;

    public ShaderPass(String name)
    {
        super(name);
        int width = mc.getWindow().getWidth();
        int height = mc.getWindow().getHeight();
        this.target = new TextureTarget(
                "shoreline_shader_" + name,
                width,
                height,
                true
        );

        this.output = new TextureTarget(
                "shoreline_shader_" + name + "_output",
                width,
                height,
                false
        );

        this.pool = new CrossFrameResourcePool(3);
    }

    public void begin()
    {
        resize();
        clearTarget();
    }

    public void bind()
    {
        RenderSystem.outputColorTextureOverride = target.getColorTextureView();
        RenderSystem.outputDepthTextureOverride = target.getDepthTextureView();
    }

    public void unbind()
    {
        RenderSystem.outputColorTextureOverride = null;
        RenderSystem.outputDepthTextureOverride = null;
    }

    public <S> void draw(AbstractShaderChain<S> chain, S provider)
    {
        clearOutput();
        chain.setUniforms(provider);
        chain.draw(target, output, pool);
        output.blitAndBlendToTexture(mc.getMainRenderTarget().getColorTextureView());
        pool.endFrame();
    }

    private void resize()
    {
        int width = mc.getWindow().getWidth();
        int height = mc.getWindow().getHeight();

        if (width == target.width && height == target.height)
        {
            return;
        }

        target.resize(width, height);
        output.resize(width, height);
    }

    public void clearTarget()
    {
        RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures(target.getColorTexture(), 0, target.getDepthTexture(), 1.0);
    }

    public void clearOutput()
    {
        RenderSystem.getDevice().createCommandEncoder().clearColorTexture(output.getColorTexture(), 0);
    }
}