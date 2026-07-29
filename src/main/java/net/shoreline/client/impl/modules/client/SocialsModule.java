package net.shoreline.client.impl.modules.client;

import lombok.Getter;
import net.shoreline.client.api.module.Category;
import net.shoreline.client.api.module.Module;
import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.api.setting.impl.BooleanSetting;
import net.shoreline.client.api.setting.impl.ColorSetting;

import java.awt.*;

@Getter
public class SocialsModule extends Module
{
    public static SocialsModule INSTANCE;

    Setting<Boolean> friends = new BooleanSetting.Builder("Friends")
            .setDescription("Won't target added friends")
            .setDefaultValue(true).build();
    Setting<Color> friendColor = new ColorSetting.Builder("FriendColor")
            .setRgb(0xFF66FFFF)
            .setDescription("The color for friends in renders")
            .build();
    Setting<Color> enemyColor = new ColorSetting.Builder("EnemyColor")
            .setRgb(0xFFFF192D)
            .setDescription("The color for enemies in renders")
            .build();

    public SocialsModule()
    {
        super("Socials", "Manages client socials", Category.CLIENT);
        INSTANCE = this;
    }
}
