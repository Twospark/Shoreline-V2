package net.shoreline.client.impl.social;

import net.minecraft.world.entity.Entity;
import net.shoreline.client.api.common.Feature;
import net.shoreline.client.impl.modules.client.SocialsModule;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SocialManager extends Feature
{
    private final Map<String, SocialType> socials = new HashMap<>();

    public SocialManager()
    {
        super("Socials", new String[] { "Friends", "Enemies" });
    }

    /* ------------- Friends ------------- */
    public void addFriend(String friendName)
    {
        addSocial(friendName, SocialType.FRIEND);
    }

    public void toggleFriend(String friendName)
    {
        if (isFriend(friendName))
        {
            removeSocial(friendName);
        }
        else
        {
            addFriend(friendName);
        }
    }

    public boolean isFriend(Entity entity)
    {
        return SocialsModule.INSTANCE.getFriends().getValue()
                && isFriendInternal(entity.getName().getString());
    }

    public boolean isFriend(String friendName)
    {
        return SocialsModule.INSTANCE.getFriends().getValue()
                && isFriendInternal(friendName);
    }

    public boolean isFriendInternal(String friendName)
    {
        return isType(friendName, SocialType.FRIEND);
    }

    /* ------------- Enemies ------------- */
    public void addEnemy(String enemyName)
    {
        socials.put(enemyName, SocialType.FRIEND);
    }

    public void removeEnemy(String enemyName)
    {
        socials.remove(enemyName);
    }

    public boolean isEnemy(Entity entity)
    {
        return isEnemy(entity.getName().getString());
    }

    public boolean isEnemy(String enemyName)
    {
        return isType(enemyName, SocialType.ENEMY);
    }

    /* ------------- Util ------------- */
    public SocialType getType(String name)
    {
        return socials.get(name);
    }

    public boolean isType(String name, SocialType type)
    {
        return socials.get(name) == type;
    }

    public void addSocial(String name, SocialType type)
    {
        socials.put(name, type);
    }

    public void removeSocial(String name)
    {
        socials.remove(name);
    }

    public Set<String> getTypes(SocialType type)
    {
        return socials.entrySet().stream().filter(entry -> entry.getValue() == type)
                .map(Map.Entry::getKey)
                .collect(Collectors.toUnmodifiableSet());
    }
}
