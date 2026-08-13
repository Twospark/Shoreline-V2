package net.shoreline.client.impl.modules.render;

import com.mojang.blaze3d.vertex.PoseStack;
import lombok.Getter;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.shoreline.client.api.module.Category;
import net.shoreline.client.api.module.Toggleable;
import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.api.setting.impl.ColorSetting;
import net.shoreline.client.api.setting.impl.NumberSetting;
import net.shoreline.client.impl.Managers;
import net.shoreline.client.impl.event.ClientEvent;
import net.shoreline.client.impl.event.render.RenderWorldEvent;
import net.shoreline.client.impl.event.render.ShaderEvent;
import net.shoreline.client.impl.render.shader.ShaderManager;
import net.shoreline.client.impl.render.shader.shaders.OutlineShader;
import net.shoreline.client.impl.render.shader.util.ShaderNodeCollector;
import net.shoreline.eventbus.api.Subscribe;

import java.awt.*;

@Getter
public class ShaderModule extends Toggleable
{
    Setting<Float> width = new NumberSetting.Builder<Float>("Width")
            .setMin(0f).setMax(5f).setDefaultValue(1f)
            .setDescription("The width of the shader outline").build();
    Setting<Float> fillOpacity = new NumberSetting.Builder<Float>("FillOpacity")
            .setMin(0.0f).setMax(1.0f).setDefaultValue(0.2f)
            .setDescription("The fill opacity").build();
    Setting<Float> outlineOpacity = new NumberSetting.Builder<Float>("OutlineOpacity")
            .setMin(0.0f).setMax(1.0f).setDefaultValue(0.8f)
            .setDescription("The outline opacity").build();
    Setting<Color> color = new ColorSetting.Builder("Color")
            .setDescription("The shader color")
            .setDefaultValue(Color.PINK).build();

    private OutlineShader outline;

    public ShaderModule()
    {
        super("Shader", "Renders a shader over entities", Category.RENDER);
    }

    @Subscribe
    public void onLoaded(ClientEvent.McLoaded event)
    {
        try
        {
            outline = new OutlineShader();
        }
        catch (net.minecraft.client.renderer.ShaderManager.CompilationException e)
        {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Subscribe
    public void onRenderWorld(RenderWorldEvent event)
    {
        if (checkNull())
        {
            return;
        }

        PoseStack poseStack = event.getPoseStack();
        Vec3 camPos = event.getCamera().pos;

        ShaderManager shader = Managers.SHADER;
        shader.begin();

        OutlineBufferSource bufferSource = new OutlineBufferSource();
        ShaderNodeCollector collector = new ShaderNodeCollector(bufferSource);

        shader.bind();
        try
        {
            for (Entity entity : mc.level.entitiesForRendering())
            {
                if (entity == mc.player)
                {
                    continue;
                }

                EntityRenderer renderer = mc.getEntityRenderDispatcher().getRenderer(entity);
                EntityRenderState state = renderer.createRenderState(entity, event.getPartialTicks());

                collector.setColor(color.getValue().getRGB());

                poseStack.pushPose();
                poseStack.translate(state.x - camPos.x, state.y - camPos.y, state.z - camPos.z);
                renderer.submit(state, event.getPoseStack(), collector, event.getCamera());
                poseStack.popPose();
            }

            collector.flush();
        }
        finally
        {
            shader.unbind();
        }
    }

    @Subscribe
    public void onShader(ShaderEvent event)
    {
        if (outline == null)
        {
            try
            {
                outline = new OutlineShader();
            }
            catch (net.minecraft.client.renderer.ShaderManager.CompilationException e)
            {
                throw new RuntimeException(e);
            }
        }

        Managers.SHADER.draw(outline, this);
    }
}
