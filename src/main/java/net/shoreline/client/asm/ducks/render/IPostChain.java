package net.shoreline.client.asm.ducks.render;

import net.minecraft.client.renderer.PostPass;

import java.util.List;

public interface IPostChain
{
    List<PostPass>  shoreline$getPasses();
}
