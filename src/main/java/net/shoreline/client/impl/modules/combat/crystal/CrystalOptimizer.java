package net.shoreline.client.impl.modules.combat.crystal;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.shoreline.client.api.common.Feature;
import net.shoreline.client.impl.event.render.RenderEntityEvent;
import net.shoreline.eventbus.EventBus;
import net.shoreline.eventbus.api.Subscribe;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class CrystalOptimizer extends Feature
{
    private final Set<Integer> deadCrystals = new ConcurrentSkipListSet<>();

    public CrystalOptimizer()
    {
        super("Crystal Optimizer");
        EventBus.getInstance().subscribe(this);
    }

    @Subscribe
    public void onRenderEntity(RenderEntityEvent event)
    {
        if (event.getEntity() instanceof EndCrystal && isDead(event.getEntity()))
        {
            event.setCanceled(true);
        }
    }

    public boolean isDead(Entity entity)
    {
        return deadCrystals.contains(entity.getId());
    }

    public void setDead(int id)
    {
        deadCrystals.add(id);
    }
}