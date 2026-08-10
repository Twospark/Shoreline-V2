package net.shoreline.client.impl.gui.font.drawers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.shoreline.client.api.interfaces.Globals;
import net.shoreline.client.impl.gui.font.CharLocation;
import net.shoreline.client.impl.gui.font.Glyph;
import net.shoreline.client.impl.gui.font.GlyphCache;
import net.shoreline.client.impl.gui.font.GlyphDrawer;
import net.shoreline.client.impl.render.ClientRenderTypes;

import java.util.List;

@RequiredArgsConstructor
public class Glyph3DDrawer implements GlyphDrawer, Globals
{
    private final MultiBufferSource.BufferSource bufferSource;
    private final PoseStack matrices;

    @Override
    public void drawGlyphs(List<CharLocation> locations, Identifier identifier)
    {
        PoseStack.Pose matrix = matrices.last();
        for (CharLocation charLocation : locations)
        {
            float x = charLocation.x();
            float y = charLocation.y();
            int color = charLocation.color();
            Glyph glyph = charLocation.glyph();
            GlyphCache owner = glyph.owner();
            float w = glyph.width();
            float h = glyph.height();
            float u1 = (float) glyph.textureWidth() / owner.getWidth();
            float v1 = (float) glyph.textureHeight() / owner.getHeight();
            float u2 = (float) (glyph.textureWidth() + glyph.width()) / owner.getWidth();
            float v2 = (float) (glyph.textureHeight() + glyph.height()) / owner.getHeight();

            RenderType type = ClientRenderTypes.FONT.apply(identifier);
            VertexConsumer consumer =  bufferSource.getBuffer(type);

            consumer.addVertex(matrix, x + 0, y + h, 0).setColor(color).setUv(u1, v2);
            consumer.addVertex(matrix, x + w, y + h, 0).setColor(color).setUv(u2, v2);
            consumer.addVertex(matrix, x + w, y + 0, 0).setColor(color).setUv(u2, v1);
            consumer.addVertex(matrix, x + 0, y + 0, 0).setColor(color).setUv(u1, v1);
        }
    }
}