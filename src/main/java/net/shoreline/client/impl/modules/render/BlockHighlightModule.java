package net.shoreline.client.impl.modules.render;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.shoreline.client.api.module.Category;
import net.shoreline.client.api.module.Toggleable;
import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.api.setting.impl.BooleanSetting;
import net.shoreline.client.api.setting.impl.EnumSetting;
import net.shoreline.client.impl.event.render.RenderWorldEvent;
import net.shoreline.client.impl.modules.client.ThemeModule;
import net.shoreline.client.impl.render.BoxRender;
import net.shoreline.client.impl.render.ClientRenderer;
import net.shoreline.eventbus.api.Subscribe;

public class BlockHighlightModule extends Toggleable
{
    Setting<BoxRender> mode = new EnumSetting.Builder<BoxRender>("Mode")
            .setDescription("Box rendering mode")
            .setDefaultValue(BoxRender.FILL).build();
    Setting<Boolean> entities = new BooleanSetting.Builder("Entities")
            .setDescription("Render entity hitboxes for debugging")
            .setDefaultValue(false).build();

    public BlockHighlightModule()
    {
        super("BlockHighlight", "Highlights the block you are looking at", Category.RENDER);
    }

    @Subscribe
    public void onRenderWorld(RenderWorldEvent event)
    {
        if (checkNull())
        {
            return;
        }

        ClientRenderer renderer = event.getRenderer();
        if (mc.hitResult instanceof BlockHitResult result)
        {
            BlockPos pos = result.getBlockPos();
            BlockState state = mc.level.getBlockState(pos);
            VoxelShape shape = state.getCollisionShape(mc.level, pos);
            if (!shape.isEmpty())
            {
                for (AABB box : shape.toAabbs())
                {
                    double minX = pos.getX() + box.minX;
                    double minY = pos.getY() + box.minY;
                    double minZ = pos.getZ() + box.minZ;
                    double maxX = pos.getX() + box.maxX;
                    double maxY = pos.getY() + box.maxY;
                    double maxZ = pos.getZ() + box.maxZ;
                    AABB bb = new AABB(minX, minY, minZ, maxX, maxY, maxZ);
                    mode.getValue().render(renderer, bb, ThemeModule.INSTANCE.getPrimary());
                }
            }
        }
        else if (mc.hitResult instanceof EntityHitResult result && entities.getValue())
        {
            Entity entity = result.getEntity();
            if (entity != null)
            {
                mode.getValue().render(renderer, entity.getBoundingBox(), ThemeModule.INSTANCE.getPrimary());
            }
        }
    }
}
