package net.shoreline.client.impl.render.shader.uniform;

import lombok.Getter;
import net.minecraft.client.renderer.UniformValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class UniformBuilder
{
    private final Map<String, List<UniformValue>> config = new HashMap<>();

    public void add(String configName, UniformValue...values)
    {
        config.put(configName, List.of(values));
    }
}