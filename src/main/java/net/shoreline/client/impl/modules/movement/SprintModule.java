package net.shoreline.client.impl.modules.movement;

import net.shoreline.client.api.module.Category;
import net.shoreline.client.api.module.Toggleable;
import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.api.setting.impl.BooleanSetting;
import net.shoreline.client.api.setting.impl.EnumSetting;

public class SprintModule extends Toggleable
{
    Setting<SprintMode> mode = new EnumSetting.Builder<SprintMode>("Mode")
            .setDescription("-Legit: Vanilla sprint that never flags.\n-Rage: Sprints in all directions.")
            .setDefaultValue(SprintMode.LEGIT).build();
    Setting<Boolean> rotate = new BooleanSetting.Builder("Rotate")
            .setDescription("Rotates the player in the sprint direction to avoid flags.")
            .setVisible(() -> mode.getValue() == SprintMode.RAGE).build();

    public SprintModule()
    {
        super("Sprint", new String[]{"AutoSprint"}, "Automaticly sprints for you", Category.MOVEMENT);
    }

    private enum SprintMode
    {
        LEGIT,
        RAGE
    }
}