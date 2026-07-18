package net.shoreline.client.api.module;

import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.api.setting.impl.BindSetting;
import net.shoreline.client.api.setting.impl.BooleanSetting;
import net.shoreline.client.api.setting.util.Bind;
import net.shoreline.eventbus.EventBus;

public class Toggleable extends Module
{
    private final Setting<Boolean> enabled = new BooleanSetting.Builder("Enabled")
            .setDescription("If the module is enabled or not.")
            .setNameAliases("Toggled")
            .setDefaultValue(false).build();
    private final Setting<Bind> bind = new BindSetting.Builder("Bind")
            .setDescription("The module keybind")
            .setNameAliases("Keybind")
            .setDefaultValue(Bind.none()).build();
    private final Setting<Boolean> notify = new BooleanSetting.Builder("Notify")
            .setDescription("Notifies in chat on toggle")
            .setVisible(() -> false)
            .setDefaultValue(false).build();

    public Toggleable(String name, String description, Category category)
    {
        this(name, new String[0], description, category);
    }

    public Toggleable(String name, String[] nameAliases, String description, Category category)
    {
        super(name, nameAliases, description, category);
        enabled.addObserver(value ->
        {
            if (value)
            {
                EventBus.getInstance().subscribe(this);
                onEnable();
            }
            else
            {
                EventBus.getInstance().unsubscribe(this);
                onDisable();
            }
        });

        reflectSettings();
    }

    public boolean isEnabled()
    {
        return enabled.getValue();
    }

    public void enable()
    {
        if (!isEnabled())
        {
            enabled.setValue(true);
        }
    }

    public void disable()
    {
        if (isEnabled())
        {
            enabled.setValue(false);
        }
    }

    public void toggle()
    {
        if (isEnabled())
        {
            disable();
        }
        else
        {
            enable();
        }
    }

    public Bind getBind()
    {
        return bind.getValue();
    }

    public void setBind(Bind bind)
    {
        this.bind.setValue(bind);
    }

    protected void onDisable()
    {

    }

    protected void onEnable()
    {

    }
}
