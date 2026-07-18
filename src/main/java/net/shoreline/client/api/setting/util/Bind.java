package net.shoreline.client.api.setting.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Bind
{
    private final int key;

    public Bind()
    {
        this(-1);
    }

    public static Bind none()
    {
        return new Bind(-1);
    }

    public static Bind fromKey(int key)
    {
        return new Bind(key);
    }
}
