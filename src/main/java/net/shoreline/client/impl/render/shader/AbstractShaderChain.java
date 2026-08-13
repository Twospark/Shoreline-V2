package net.shoreline.client.impl.render.shader;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.CrossFrameResourcePool;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.ShaderManager;
import net.minecraft.resources.Identifier;
import net.shoreline.client.api.common.Feature;
import net.shoreline.client.impl.render.shader.uniform.UniformBuilder;
import net.shoreline.client.impl.render.shader.uniform.UniformWriter;
import org.joml.Vector2f;

import java.util.List;
import java.util.Map;

public abstract class AbstractShaderChain<S> extends Feature
{
    protected static final Identifier BASE = Identifier.fromNamespaceAndPath("shoreline", "post/base");

    private final UniformWriter writer;
    private final PostChain chain;

    public AbstractShaderChain(String name) throws ShaderManager.CompilationException
    {
        super(name);
        Projection projection = new Projection();
        projection.setSize(0.1f, 1000.0f);
        this.writer = new UniformWriter();
        this.chain = PostChain.load(
                createConfig(getName()), mc.getTextureManager(),
                LevelTargetBundle.MAIN_TARGETS,
                Identifier.fromNamespaceAndPath("shoreline", getName()),
                projection, new ProjectionMatrixBuffer("shoreline_" + getName()));
    }

    public abstract PostChainConfig createConfig(String name);

    public abstract void buildUniforms(S provider, UniformBuilder builder);

    @SuppressWarnings("deprecation")
    public void draw(RenderTarget target, CrossFrameResourcePool pool)
    {
        chain.process(target, pool);
    }

    public void setUniforms(S provider)
    {
        UniformBuilder builder = new UniformBuilder();
        buildAllUniforms(provider, builder);
        writer.setUniforms(chain, builder);
    }

    protected void buildAllUniforms(S provider, UniformBuilder builder)
    {
        builder.add("BaseConfig", vec2(mc.getWindow().getWidth(), mc.getWindow().getHeight()));
        buildUniforms(provider, builder);
    }

    @Override
    public String[] getAliases()
    {
        return new String[0];
    }

    protected UniformValue f(float floatValue)
    {
        return new UniformValue.FloatUniform(floatValue);
    }

    protected UniformValue i(int intValue)
    {
        return new UniformValue.IntUniform(intValue);
    }

    protected UniformValue vec2(float f1, float f2)
    {
        return new UniformValue.Vec2Uniform(new Vector2f(f1, f2));
    }

    public Identifier getIdentifier()
    {
        return Identifier.fromNamespaceAndPath("shoreline", "post/" + name);
    }
}