package net.shoreline.client.impl.event.render;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;

@RequiredArgsConstructor
@Getter
@Setter
public class AddEntityEvent
{
    private final Vec3 pos;
    private final int entityId;
    private final EntityType<?> type;}
