package net.shoreline.client.impl.render.shader.shaders;

import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostChainConfig;
import net.shoreline.client.impl.modules.render.ShaderModule;
import net.shoreline.client.impl.render.shader.AbstractShaderChain;
import net.shoreline.client.impl.render.shader.uniform.UniformBuilder;

import java.util.List;
import java.util.Map;

public class OutlineShader extends AbstractShaderChain<ShaderModule>
{
    public OutlineShader()
    {
        super("outline");
    }

    @Override
    public PostChainConfig createConfig(String name)
    {
        PostChainConfig.TargetInput outlineInput = new PostChainConfig.TargetInput(
                "Texture",
                SOURCE,
                false,
                false
        );

        PostChainConfig.Pass pass = new PostChainConfig.Pass(
                BASE,
                getIdentifier(),
                List.of(outlineInput),
                PostChain.MAIN_TARGET_ID,
                Map.of("OutlineConfig", List.of(
                        i(1),
                        f(0.5f),
                        f(1.0f)))
        );

        return new PostChainConfig(Map.of(), List.of(pass));
    }

    @Override
    public void buildUniforms(ShaderModule provider, UniformBuilder builder)
    {
        builder.add("OutlineConfig",
            f(provider.getWidth().getValue()),
            f(provider.getFillOpacity().getValue()),
            f(provider.getOutlineOpacity().getValue()));
    }
}