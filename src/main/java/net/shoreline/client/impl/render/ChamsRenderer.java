package net.shoreline.client.impl.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.shoreline.client.api.interfaces.Globals;
import net.shoreline.client.asm.ducks.render.IRenderType;
import org.joml.Matrix4f;

public enum ChamsRenderer implements Globals
{
    NONE,
    CHAMS,
    WIREFRAME,
    BOTH;

    private static final SubmitNodeStorage renderCommandQueue = new SubmitNodeStorage();

    private static final FeatureRenderDispatcher renderDispatcher = new FeatureRenderDispatcher(
            renderCommandQueue,
            mc.getModelManager(),
            MyVertexConsumerProvider.INSTANCE,
            mc.getAtlasManager(),
            NoopOutlineVertexConsumerProvider.INSTANCE,
            NoopImmediateVertexConsumerProvider.INSTANCE,
            mc.font,
            mc.gameRenderer.getGameRenderState()
    );

    private static final PoseStack matrices = new PoseStack();
    private static final Matrix4f matrix = matrices.last().pose();;

    private static ChamsRenderer chams;
    private static ClientRenderer renderer;
    private static Vec3 position;
    private static int color;
    private static float transparency;

    @SuppressWarnings("unchecked")
    public static void render(ClientRenderer clientRenderer, ChamsRenderer chams, Entity entity, float partialTicks, int color, float transparency)
    {
        ChamsRenderer.renderer = clientRenderer;
        ChamsRenderer.chams = chams;
        ChamsRenderer.color = color;
        ChamsRenderer.transparency = transparency;

        Vec3 interp = Interpolation.getRenderPosition(entity, partialTicks);

        var renderer = (EntityRenderer<Entity, EntityRenderState>) mc.getEntityRenderDispatcher().getRenderer(entity);
        var state = renderer.createRenderState(entity, partialTicks);

        position = interp.add(renderer.getRenderOffset(state));

        renderer.submit(state, matrices, renderCommandQueue, mc.gameRenderer.getGameRenderState().levelRenderState.cameraRenderState);
        renderDispatcher.renderAllFeatures();
        renderCommandQueue.endFrame();
    }

    private static class MyVertexConsumerProvider extends MultiBufferSource.BufferSource
    {
        public static final MyVertexConsumerProvider INSTANCE = new MyVertexConsumerProvider();
        private final Object2ObjectOpenHashMap<RenderType, MyVertexConsumer> buffers = new Object2ObjectOpenHashMap<>();

        protected MyVertexConsumerProvider()
        {
            super(null, null);
        }

        @Override
        public VertexConsumer getBuffer(RenderType layer)
        {
            if (((IRenderType) layer).shoreline$getState().outputTarget == OutputTarget.ITEM_ENTITY_TARGET)
            {
                return NoopVertexConsumer.INSTANCE;
            }

            return buffers.computeIfAbsent(layer, _ -> new MyVertexConsumer());
        }

        @Override
        public void endBatch()
        {
            throw new RuntimeException();
        }

        @Override
        public void endBatch(RenderType type)
        {
            throw new RuntimeException();
        }
    }

    private static class MyVertexConsumer implements VertexConsumer
    {
        private final float[] xs = new float[4];
        private final float[] ys = new float[4];
        private final float[] zs = new float[4];

        private int i = 0;

        @Override
        public VertexConsumer addVertex(float x, float y, float z)
        {
            xs[i] = x;
            ys[i] = y;
            zs[i] = z;

            i++;

            if (i == 4)
            {
                if ((chams == CHAMS || chams == BOTH))
                {
                    renderer.render(ClientRenderTypes.QUADS, buffer ->
                    {
                        int boxColor = ColorUtil.withTransparency(color, transparency).getRGB();
                        buffer.addVertex(matrix, (float) (position.x() + xs[0]), (float) (position.y() + ys[0]), (float) (position.z() + zs[0])).setColor(boxColor);
                        buffer.addVertex(matrix, (float) (position.x() + xs[1]), (float) (position.y() + ys[1]), (float) (position.z() + zs[1])).setColor(boxColor);
                        buffer.addVertex(matrix, (float) (position.x() + xs[2]), (float) (position.y() + ys[2]), (float) (position.z() + zs[2])).setColor(boxColor);
                        buffer.addVertex(matrix, (float) (position.x() + xs[3]), (float) (position.y() + ys[3]), (float) (position.z() + zs[3])).setColor(boxColor);
                    });
                }

                if ((chams == WIREFRAME || chams == BOTH))
                {
                    renderer.render(ClientRenderTypes.DEBUG_LINES, buffer ->
                    {
                        int lineColor = (color & 0x00FFFFFF) | 0xFF000000;
                        lineColor = ColorUtil.withTransparency(lineColor, transparency).getRGB();
                        buffer.addVertex(matrix, (float) (position.x + xs[0]), (float) (position.y + ys[0]), (float) (position.z + zs[0])).setColor(lineColor);
                        buffer.addVertex(matrix, (float) (position.x + xs[1]), (float) (position.y + ys[1]), (float) (position.z + zs[1])).setColor(lineColor);
                        buffer.addVertex(matrix, (float) (position.x + xs[1]), (float) (position.y + ys[1]), (float) (position.z + zs[1])).setColor(lineColor);
                        buffer.addVertex(matrix, (float) (position.x + xs[2]), (float) (position.y + ys[2]), (float) (position.z + zs[2])).setColor(lineColor);
                        buffer.addVertex(matrix, (float) (position.x + xs[2]), (float) (position.y + ys[2]), (float) (position.z + zs[2])).setColor(lineColor);
                        buffer.addVertex(matrix, (float) (position.x + xs[3]), (float) (position.y + ys[3]), (float) (position.z + zs[3])).setColor(lineColor);
                    });
                }

                i = 0;
            }

            return this;
        }

        @Override
        public VertexConsumer setColor(int red, int green, int blue, int alpha)
        {
            return this;
        }

        @Override
        public VertexConsumer setColor(int argb)
        {
            return this;
        }

        @Override
        public VertexConsumer setUv(float u, float v)
        {
            return this;
        }

        @Override
        public VertexConsumer setUv1(int u, int v)
        {
            return this;
        }

        @Override
        public VertexConsumer setUv2(int u, int v)
        {
            return this;
        }

        @Override
        public VertexConsumer setNormal(float x, float y, float z)
        {
            return this;
        }

        @Override
        public VertexConsumer setLineWidth(float width)
        {
            return this;
        }
    }

    public static class NoopOutlineVertexConsumerProvider extends OutlineBufferSource
    {
        public static final NoopOutlineVertexConsumerProvider INSTANCE = new NoopOutlineVertexConsumerProvider();

        private NoopOutlineVertexConsumerProvider() {}

        @Override
        public VertexConsumer getBuffer(RenderType layer)
        {
            return NoopVertexConsumer.INSTANCE;
        }

        @Override
        public void endOutlineBatch() {}
    }

    public static class NoopVertexConsumer implements VertexConsumer
    {
        public static final NoopVertexConsumer INSTANCE = new NoopVertexConsumer();

        private NoopVertexConsumer()
        {
        }

        @Override
        public VertexConsumer addVertex(float x, float y, float z)
        {
            return this;
        }

        @Override
        public VertexConsumer setColor(int red, int green, int blue, int alpha)
        {
            return this;
        }

        @Override
        public VertexConsumer setColor(int argb)
        {
            return this;
        }

        @Override
        public VertexConsumer setUv(float u, float v)
        {
            return this;
        }

        @Override
        public VertexConsumer setUv1(int u, int v)
        {
            return this;
        }

        @Override
        public VertexConsumer setUv2(int u, int v)
        {
            return this;
        }

        @Override
        public VertexConsumer setNormal(float x, float y, float z)
        {
            return this;
        }

        @Override
        public VertexConsumer setLineWidth(float width)
        {
            return this;
        }
    }

    public static class NoopImmediateVertexConsumerProvider extends MultiBufferSource.BufferSource
    {
        public static final NoopImmediateVertexConsumerProvider INSTANCE = new NoopImmediateVertexConsumerProvider();

        private NoopImmediateVertexConsumerProvider()
        {
            super(null, null);
        }

        @Override
        public VertexConsumer getBuffer(RenderType layer)
        {
            return NoopVertexConsumer.INSTANCE;
        }

        @Override
        public void endBatch() {}

        @Override
        public void endBatch(RenderType layer) {}
    }
}
