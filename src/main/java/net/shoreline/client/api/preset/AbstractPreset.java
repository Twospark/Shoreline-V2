package net.shoreline.client.api.preset;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.shoreline.client.api.interfaces.Identifiable;
import net.shoreline.client.impl.Managers;
import net.shoreline.client.impl.config.AbstractConfig;
import net.shoreline.client.impl.config.util.IOUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class AbstractPreset<T extends Identifiable> extends AbstractConfig
{
    protected final Collection<T> values;

    public AbstractPreset(String name, Collection<T> values) throws IOException
    {
        super(Managers.CONFIG.getPresetDirectory(), name);
        this.values = values;
    }

    @Override
    public void saveFile() throws IOException
    {
        JsonObject object = new JsonObject();
        for (T value : values)
        {
            JsonObject cObject = new JsonObject();
            apply(value, cObject);
            object.add(value.getId(), cObject);
        }

        IOUtils.writeFile(getPath(), GSON.toJson(object));
    }

    @Override
    public void loadFile() throws IOException
    {
        Path filepath = getPath();
        if (!Files.exists(filepath))
        {
            return;
        }

        JsonObject object = parseJson(IOUtils.readFile(filepath), JsonObject.class);
        if (object == null)
        {
            return;
        }

        for (Map.Entry<String, JsonElement> entry : object.entrySet())
        {
            T value = values.stream()
                    .filter(v -> v.getId().equalsIgnoreCase(entry.getKey()))
                    .findFirst()
                    .orElse(null);

            if (value == null)
            {
                continue;
            }

            JsonObject vObject = entry.getValue().getAsJsonObject();
            load(value, vObject);
        }
    }

    protected abstract void apply(T container, JsonObject object);

    protected abstract void load(T container, JsonObject object);
}
