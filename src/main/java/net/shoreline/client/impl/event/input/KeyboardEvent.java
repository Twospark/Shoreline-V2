package net.shoreline.client.impl.event.input;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class KeyboardEvent
{
    private final int key;
    private final int action;
    private final int modifiers;
}
