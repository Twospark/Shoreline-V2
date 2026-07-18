package net.shoreline.client.api.registry;

import lombok.Getter;
import net.shoreline.client.api.common.Feature;

@Getter
public class RegistryFeature<T> extends Feature
{
    private final Registry<T> registry;

    public RegistryFeature(String name, Registry<T> registry)
    {
        this(name, new String[0], registry);
    }

    public RegistryFeature(String name, String[] nameAliases, Registry<T> registry)
    {
        super(name, nameAliases);
        this.registry = registry;
    }
}
