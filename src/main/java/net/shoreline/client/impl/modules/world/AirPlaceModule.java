package net.shoreline.client.impl.modules.world;

import net.shoreline.client.api.module.Category;
import net.shoreline.client.api.module.Toggleable;
import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.api.setting.impl.BooleanSetting;

public class AirPlaceModule extends Toggleable
{
    public static AirPlaceModule INSTANCE;

    Setting<Boolean> grim = new BooleanSetting.Builder("Grim")
            .setDescription("Place blocks in the air on Grim servers")
            .setDefaultValue(false).build();

    public AirPlaceModule()
    {
        super("AirPlace", "Places blocks in the air", Category.WORLD);
        INSTANCE = this;
    }

    public boolean isGrim()
    {
        return grim.getValue();
    }

    public boolean isForceAirPlace()
    {
        return false;
    }
}
