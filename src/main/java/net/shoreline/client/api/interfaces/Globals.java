package net.shoreline.client.api.interfaces;

import net.minecraft.client.Minecraft;

import java.util.Random;

public interface Globals
{
    Random RANDOM = new Random();

    Minecraft mc = Minecraft.getInstance();
}
