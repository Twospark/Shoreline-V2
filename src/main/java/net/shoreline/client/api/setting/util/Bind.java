package net.shoreline.client.api.setting.util;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
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
}
