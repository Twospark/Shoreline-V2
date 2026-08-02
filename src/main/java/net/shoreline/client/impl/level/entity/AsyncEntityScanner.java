package net.shoreline.client.impl.level.entity;

import com.google.common.collect.Lists;
import lombok.Setter;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.shoreline.client.api.interfaces.Globals;
import net.shoreline.client.impl.Managers;
import net.shoreline.client.impl.level.entity.state.EntityState;
import net.shoreline.client.impl.level.entity.state.LivingEntityState;
import net.shoreline.client.impl.social.SocialType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
public abstract class AsyncEntityScanner implements EntityProvider, Globals
{
    /** A EntityState of the local player. */
    private volatile LivingEntityState localPlayer;
    /** A map of all players in range. */
    private volatile Map<Integer, LivingEntityState> all = new HashMap<>();
    /** A list of all players in range. */
    private volatile List<LivingEntityState> allList = new ArrayList<>();
    /** A list of all friends in range. */
    private volatile List<LivingEntityState> friends = new ArrayList<>();
    /** A list of all enemies in range. */
    private volatile List<LivingEntityState> enemies = new ArrayList<>();
    /** A list of all non-friended players in range. */
    private volatile List<LivingEntityState> players = new ArrayList<>();
    /** A map of all entities in range */
    private volatile Map<Integer, EntityState> entities = new HashMap<>();
    /** A list of all entities in range */
    private volatile List<EntityState> entitiesList = new ArrayList<>();

    public abstract int getRadius();

    public void createEntityLookup()
    {
        createEntityLookup(mc.level);
    }

    public void createEntityLookup(Level level)
    {
        createEntityLookup(level, mc.player.getEyePosition());
    }

    public void createEntityLookup(Level level, Vec3 vec)
    {
        int radius = getRadius();
        AABB bb = new AABB(vec.x - radius,
                vec.y - radius,
                vec.z - radius,
                vec.x + radius,
                vec.y + radius,
                vec.z + radius
        );

        Map<Integer, EntityState> newEntities = new HashMap<>();
        Map<Integer, LivingEntityState> newAll     = new HashMap<>();

        float rSq = Mth.square(radius);
        for (Entity entity : level.getEntities(mc.player, bb, e -> e.distanceToSqr(vec) <= rSq))
        {
            if (entity == mc.player)
            {
                continue;
            }

            if (entity instanceof Player player)
            {
                newAll.put(player.getId(), new LivingEntityState(player));
            }

            newEntities.put(entity.getId(), new EntityState(entity));
        }

        this.localPlayer = new LivingEntityState(mc.player);
        this.entities = newEntities;
        this.entitiesList = Lists.newArrayList(entities.values());
        sort(newAll);
    }

    public void sort(Map<Integer, LivingEntityState> map)
    {
        List<LivingEntityState> friends = new ArrayList<>();
        List<LivingEntityState> enemies = new ArrayList<>();
        List<LivingEntityState> players = new ArrayList<>();

        for (LivingEntityState playerState : map.values())
        {
            SocialType type = Managers.SOCIAL.getType(playerState.getName());
            if (type == null)
            {
                players.add(playerState);
                continue;
            }

            switch (type)
            {
                case FRIEND -> friends.add(playerState);
                case ENEMY -> enemies.add(playerState);
            }
        }

        this.players = players;
        this.friends = friends;
        this.enemies = enemies;
        this.all = map;
        this.allList = Lists.newArrayList(all.values());
    }

    @Override
    public LivingEntityState getLocalPlayer()
    {
        return localPlayer;
    }

    @Override
    public LivingEntityState getPlayer(int id)
    {
        return all.get(id);
    }

    @Override
    public EntityState getEntity(int id)
    {
        return entities.get(id);
    }

    @Override
    public List<LivingEntityState> getAllPlayers()
    {
        return allList;
    }

    @Override
    public List<LivingEntityState> getPlayers()
    {
        return players;
    }

    @Override
    public List<LivingEntityState> getFriends()
    {
        return friends;
    }

    @Override
    public List<LivingEntityState> getEnemies()
    {
        return enemies;
    }

    @Override
    public List<EntityState> getEntities()
    {
        return entitiesList;
    }
}
