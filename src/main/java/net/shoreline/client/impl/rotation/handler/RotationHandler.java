package net.shoreline.client.impl.rotation.handler;

import lombok.Getter;
import net.minecraft.client.player.LocalPlayer;
import net.shoreline.client.impl.Managers;
import net.shoreline.client.impl.rotation.util.Rotation;

public class RotationHandler
{
    @Getter
    private Rotation cachedRotation;

    public void applyRotations(LocalPlayer player)
    {
        cachedRotation = new Rotation(player);
        Rotation curr = Managers.ROTATION.getClientRotation();
        curr.apply(player);
    }

    public void revertRotations(LocalPlayer player)
    {
        if (player == null || cachedRotation == null)
        {
            return;
        }

        cachedRotation.apply(player);
        cachedRotation = null;
    }

    public void resetRotations(Rotation playerRotation, float speed)
    {
        if (!Managers.ROTATION.hasClientRotation())
        {
            return;
        }

        Managers.ROTATION.clearClientRotation();
    }
}
