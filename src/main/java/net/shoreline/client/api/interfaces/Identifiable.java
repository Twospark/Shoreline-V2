package net.shoreline.client.api.interfaces;

public interface Identifiable
{
    /** Hva er ditt navn? */
    String getName();

    String[] getAliases();

    default String getDisplayName()
    {
        return getName();
    }

    default String getId()
    {
        return getName().toLowerCase();
    }
}
