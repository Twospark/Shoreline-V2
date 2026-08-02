package net.shoreline.client.impl.level;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.shoreline.client.impl.block.AsyncBlockScanner;
import net.shoreline.client.impl.level.entity.AsyncEntityScanner;
import net.shoreline.client.impl.level.entity.EntityProvider;
import net.shoreline.client.impl.level.entity.state.EntityState;
import net.shoreline.client.impl.level.entity.state.LivingEntityState;

import java.util.List;

public abstract class AsyncLevelScanner<T> extends AsyncBlockScanner<T>
        implements EntityProvider
{
    protected final AsyncEntityScanner entityScanner = new AsyncEntityScanner()
    {
        @Override
        public int getRadius()
        {
            return AsyncLevelScanner.this.getRadius();
        }
    };

    public void createLevelLookup(ClientLevel level, LocalPlayer player, boolean sphere)
    {
        createEntityLookup(level, player.getEyePosition());
        if (sphere)
        {
            createSphere(level, BlockPos.containing(player.getEyePosition()));
        }
        else
        {
            createCube(level, BlockPos.containing(player.getEyePosition()));
        }
    }

    @Override
    public void createCube(ClientLevel world, BlockPos center)
    {
        super.createCube(world, center);
        createEntityLookup(world, center.getCenter());
    }

    @Override
    public void createSphere(ClientLevel world, BlockPos center)
    {
        super.createSphere(world, center);
        createEntityLookup(world, center.getCenter());
    }

    public void createEntityLookup(ClientLevel world, Vec3 center)
    {
        entityScanner.createEntityLookup(world, center);
    }

    @Override
    public LivingEntityState getLocalPlayer()
    {
        return entityScanner.getLocalPlayer();
    }

    @Override
    public LivingEntityState getPlayer(int id)
    {
        return entityScanner.getPlayer(id);
    }

    @Override
    public EntityState getEntity(int id)
    {
        return entityScanner.getEntity(id);
    }

    @Override
    public List<LivingEntityState> getAllPlayers()
    {
        return entityScanner.getAllPlayers();
    }

    @Override
    public List<LivingEntityState> getPlayers()
    {
        return entityScanner.getPlayers();
    }

    @Override
    public List<LivingEntityState> getFriends()
    {
        return entityScanner.getFriends();
    }

    @Override
    public List<LivingEntityState> getEnemies()
    {
        return entityScanner.getEnemies();
    }

    @Override
    public List<EntityState> getEntities()
    {
        return entityScanner.getEntities();
    }
}
