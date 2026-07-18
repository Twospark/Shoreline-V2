package net.shoreline.client.impl.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.Strictness;
import lombok.Getter;
import net.shoreline.client.api.interfaces.Identifiable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class AbstractConfig implements Identifiable
{
    protected static final Gson GSON = new GsonBuilder()
            .setStrictness(Strictness.LENIENT)
            .setPrettyPrinting()
            .create();

    private final String name;
    @Getter
    private final Path path;

    public AbstractConfig(Path directory,
                          String pathIn)
            throws IOException
    {
        name = pathIn;
        path = directory.resolve(String.format("%s.cfg", pathIn).toLowerCase());
        if (!Files.exists(path))
        {
            Files.createFile(path);
        }
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String[] getAliases()
    {
        return new String[0];
    }

    protected <T> T parseJson(String json, Class<T> type)
    {
        try
        {
            return GSON.fromJson(json, type);
        }
        catch (JsonSyntaxException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public abstract void saveFile() throws IOException;

    public abstract void loadFile() throws IOException;
}
