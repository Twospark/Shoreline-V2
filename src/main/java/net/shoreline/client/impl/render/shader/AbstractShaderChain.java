package net.shoreline.client.impl.render.shader;

import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.CrossFrameResourcePool;
import com.mojang.blaze3d.resource.ResourceHandle;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.ShaderManager;
import net.minecraft.resources.Identifier;
import net.shoreline.client.api.common.Feature;
import net.shoreline.client.impl.render.shader.uniform.UniformBuilder;
import net.shoreline.client.impl.render.shader.uniform.UniformWriter;
import org.joml.Vector2f;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class AbstractShaderChain<S> extends Feature
{
    protected static final Identifier BASE =
        Identifier.fromNamespaceAndPath(
                "shoreline",
                "post/base"
        );

    protected static final Identifier SOURCE =
        Identifier.fromNamespaceAndPath(
                "shoreline",
                "source"
        );

    private final UniformWriter writer;
    private final PostChain chain;

    public AbstractShaderChain(String name)
    {
        super(name);
        this.writer = new UniformWriter();
        try
        {
            this.chain = PostChain.load(
                    createConfig(getName()), mc.getTextureManager(),
                    Set.of(SOURCE, PostChain.MAIN_TARGET_ID),
                    Identifier.fromNamespaceAndPath("shoreline", getName()),
                    new Projection(), new ProjectionMatrixBuffer("shoreline_" + getName()));
        }
        catch (ShaderManager.CompilationException e)
        {
            throw new RuntimeException(e);
        }
    }

    public abstract PostChainConfig createConfig(String name);

    public abstract void buildUniforms(S provider, UniformBuilder builder);

    public void draw(RenderTarget target, RenderTarget output, CrossFrameResourcePool pool)
    {
        FrameGraphBuilder frame = new FrameGraphBuilder();
        ResourceHandle<RenderTarget> targetHandle = frame.importExternal("shoreline_target", target);
        ResourceHandle<RenderTarget> outputHandle = frame.importExternal("shoreline_output", output);

        ShaderTargetBundle targets = new ShaderTargetBundle();
        targets.put(PostChain.MAIN_TARGET_ID, outputHandle);
        targets.put(SOURCE, targetHandle);

        chain.addToFrame(frame, output.width, output.height, targets);
        frame.execute(pool);
    }

    public void setUniforms(S provider)
    {
        UniformBuilder builder = new UniformBuilder();
        buildUniforms(provider, builder);
        writer.setUniforms(chain, builder);
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

    private static final class ShaderTargetBundle implements PostChain.TargetBundle
    {
        private final Map<Identifier, ResourceHandle<RenderTarget>> targets = new HashMap<>();

        public void put(Identifier id, ResourceHandle<RenderTarget> target)
        {
            targets.put(id, target);
        }

        @Override
        public ResourceHandle<RenderTarget> get(Identifier id)
        {
            return targets.get(id);
        }

        @Override
        public void replace(Identifier id, ResourceHandle<RenderTarget> target)
        {
            targets.put(id, target);
        }
    }
}