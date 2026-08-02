package net.shoreline.client.util.level;

import com.google.common.collect.Lists;
import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;

import java.util.List;
import java.util.function.Predicate;

@UtilityClass
public class LevelUtil
{
    public List<Entity> collectEntitiesInBox(AABB boundingBox)
    {
        return collectEntitiesInBox(Entity.class, boundingBox, null);
    }

    public <T extends Entity> List<T> collectEntitiesInBox(Class<T> entityClass,
                                                           AABB box,
                                                           Predicate<? super Entity> predicate)
    {
        List<T> entities = Lists.newArrayList();
        for (Entity entity : Minecraft.getInstance().level.entitiesForRendering())
        {
            if (entity == null || entity.isRemoved())
            {
                continue;
            }

            if (!entityClass.isAssignableFrom(entity.getClass()) || (predicate != null && !predicate.test(entity)))
            {
                continue;
            }

            if (Shapes.joinIsNotEmpty(Shapes.create(box), Shapes.create(entity.getBoundingBox()), BooleanOp.AND))
            {
                entities.add((T) entity);
            }
        }

        return entities;
    }
}
