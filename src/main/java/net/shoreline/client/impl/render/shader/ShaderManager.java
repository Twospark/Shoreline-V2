package net.shoreline.client.impl.render.shader;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.resource.CrossFrameResourcePool;
import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Getter;
import net.shoreline.client.api.common.Feature;

// doesn't really need to be a manager.
@Getter
public class ShaderManager extends Feature
{
    private final RenderTarget target;
    private final CrossFrameResourcePool pool;

    public ShaderManager()
    {
        super("Shaders");
        this.target = new TextureTarget("shoreline_shader",
                mc.getWindow().getWidth(), mc.getWindow().getHeight(), true);
        this.pool = new CrossFrameResourcePool(3);
    }

    public void begin()
    {
        resize();
        clear();
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
        chain.setUniforms(provider);
        chain.draw(target, pool);
        target.blitAndBlendToTexture(mc.getMainRenderTarget().getColorTextureView());
        pool.endFrame();
    }

    private void resize()
    {
        int width = mc.getWindow().getWidth();
        int height = mc.getWindow().getHeight();
        if (width != target.width || height != target.height)
        {
            target.resize(width, height);
        }
    }

    private void clear()
    {
        RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures(target.getColorTexture(), 0, target.getDepthTexture(), 1.0);
    }
}