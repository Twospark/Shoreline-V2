package net.shoreline.client.impl.modules.client;

import lombok.Getter;
import net.shoreline.client.api.module.Category;
import net.shoreline.client.api.module.Concurrent;
import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.api.setting.impl.BooleanSetting;
import net.shoreline.client.api.setting.impl.EnumSetting;
import net.shoreline.client.impl.rotation.util.Rotation;

@Getter
public class RotationsModule extends Concurrent
{
    public static RotationsModule INSTANCE;

    Setting<Boolean> showRotations = new BooleanSetting.Builder("ShowRotations")
            .setDescription("Renders the serverside rotations")
            .setDefaultValue(true).build();
    Setting<Boolean> noServerRotate = new BooleanSetting.Builder("NoRotate")
            .setDescription("Prevents the server from forcing rotations")
            .setDefaultValue(false).build();
    Setting<Boolean> raytrace = new BooleanSetting.Builder("Raytrace")
            .setDescription("Uses server rotations when raytracing crosshair")
            .setDefaultValue(false).build();
    Setting<MoveFix> moveFix = new EnumSetting.Builder<MoveFix>("MoveFix")
            .setDescription("Applies movement corrections when rotating")
            .setDefaultValue(MoveFix.NONE).build();
    Setting<Boolean> fixTravel = new BooleanSetting.Builder("FixInAir")
            .setVisibilityDependant(true)
            .setDescription("Fixes the movement while in air")
            .setVisible(() -> moveFix.getValue() != MoveFix.NONE)
            .setDefaultValue(false).build();
    Setting<Boolean> normalize = new BooleanSetting.Builder("Normalize")
            .setVisibilityDependant(true)
            .setDescription("Normalizes the movement vector")
            .setVisible(() -> moveFix.getValue() != MoveFix.NONE)
            .setDefaultValue(false).build();
    Setting<Boolean> gcdFix = new BooleanSetting.Builder("MouseSensFix")
            .setVisibilityDependant(true)
            .setDescription("Corrects rotations based on mouse sensitivity")
            .setVisible(() -> moveFix.getValue() != MoveFix.NONE)
            .setDefaultValue(true).build();
    Setting<Boolean> itemUseFix = new BooleanSetting.Builder("ItemUseFix")
            .setDescription("Fixes rotations when using items")
            .setDefaultValue(false).build();
    Setting<Boolean> tickSync = new BooleanSetting.Builder("TickSync")
            .setDescription("Sends rotation packets every tick")
            .setDefaultValue(false).build();
    Setting<Boolean> lookSync = new BooleanSetting.Builder("LookSync")
            .setDescription("Sends rotation packets when player look changes")
            .setDefaultValue(false).build();

    private Rotation clientRotation;

    public RotationsModule()
    {
        super("Rotations", "Manages Shorelines internal RotationManager", Category.CLIENT);
        INSTANCE = this;
    }

    public enum MoveFix
    {
        NORMAL,
        GRIM,
        NONE
    }
}
