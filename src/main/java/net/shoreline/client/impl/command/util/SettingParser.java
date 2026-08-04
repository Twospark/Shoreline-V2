package net.shoreline.client.impl.command.util;

import com.google.common.collect.Lists;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.SharedSuggestionProvider;
import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.api.setting.impl.*;
import net.shoreline.client.impl.command.util.impl.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@SuppressWarnings({"unchecked", "rawtypes"})
public class SettingParser
{
    private static final Map<Class<? extends Setting>,
        ISettingParser<?, ?>> PARSERS = new HashMap<>();
    private static final Map<Class<? extends Setting>,
        Function<Setting<?>, Collection<String>>> SUGGESTIONS = new HashMap<>();

    static
    {
        PARSERS.put(BooleanSetting.class,         new BooleanSettingParser());
        PARSERS.put(ToggleableSettingGroup.class, new BooleanSettingParser());
        PARSERS.put(ColorSetting.class,           new ColorSettingParser());
        PARSERS.put(NumberSetting.class,          new NumberSettingParser<>());
        PARSERS.put(EnumSetting.class,            new EnumSettingParser<>());
        PARSERS.put(BindSetting.class,            new BindSettingParser());
    }

    @SuppressWarnings("unchecked")
    public static <T> boolean parseString(Setting<T> setting, String string)
    {
        ISettingParser<T, Setting<T>> parser =
                (ISettingParser<T, Setting<T>>) PARSERS.get(setting.getClass());

        if (parser != null)
        {
            return parser.parseString(setting, string);
        }

        return false;
    }

    public static CompletableFuture<Suggestions> getSuggestions(SuggestionsBuilder builder, Setting<?> config)
    {
        Function<Setting<?>, Collection<String>> provider = SUGGESTIONS.get(config.getClass());
        if (provider == null)
        {
            return SharedSuggestionProvider.suggest(Lists.newArrayList(), builder);
        }

        List<String> result = new ArrayList<>(provider.apply(config));
        return SharedSuggestionProvider.suggest(result, builder);
    }

    private static void registerSuggestions()
    {
        registerSuggestion(BooleanSetting.class, "true", "false", "toggle");
        registerSuggestion(ToggleableSettingGroup.class, "true", "false", "toggle");
    }

    public static void registerSuggestion(Class<? extends Setting> clazz,
                                          Function<Setting<?>, Collection<String>> suggestionFunction)
    {
        SUGGESTIONS.putIfAbsent(clazz, suggestionFunction);
    }

    public static void registerSuggestion(Class<? extends Setting> clazz,
                                          String...suggestions)
    {
        registerSuggestion(clazz, config ->  Arrays.stream(suggestions).toList());
    }
}
