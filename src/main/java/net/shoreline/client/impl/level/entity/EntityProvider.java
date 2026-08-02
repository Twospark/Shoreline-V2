package net.shoreline.client.impl.level.entity;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.shoreline.client.impl.level.entity.state.EntityState;
import net.shoreline.client.impl.level.entity.state.LivingEntityState;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public interface EntityProvider
{
    LivingEntityState getLocalPlayer();

    LivingEntityState getPlayer(int id);

    EntityState getEntity(int id);

    List<LivingEntityState> getAllPlayers();

    List<LivingEntityState> getPlayers();

    List<LivingEntityState> getFriends();

    List<LivingEntityState> getEnemies();

    List<EntityState> getEntities();

    default List<EntityState> getOtherEntities(EntityState except, AABB aabb)
    {
        List<EntityState> result = new CopyOnWriteArrayList<>();
        for (EntityState state : getEntities())
        {
            if (state.equals(except))
            {
                continue;
            }

            if (state.getBoundingBox().intersects(aabb))
            {
                result.add(state);
            }
        }

        return result;
    }
}
