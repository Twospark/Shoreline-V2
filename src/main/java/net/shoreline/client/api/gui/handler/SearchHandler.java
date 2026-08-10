package net.shoreline.client.api.gui.handler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.shoreline.client.api.interfaces.Globals;
import net.shoreline.client.impl.Managers;
import net.shoreline.client.impl.render.ColorUtil;

import java.awt.*;

@Getter
@Setter
@RequiredArgsConstructor
public class SearchHandler implements Globals
{
    private final TextHandler textHandler = new TextHandler();
    private String completion;
    private boolean searching;

    public String getSearch()
    {
        return textHandler.getText() != null ? textHandler.getText() : "";
    }

    public void render(GuiGraphicsExtractor graphics, float partialTicks)
    {
        float scaledWidth = mc.getWindow().getGuiScaledWidth();
        float scaledHeight = mc.getWindow().getGuiScaledHeight();

        String label = searching ? "Esc to stop Searching" : "Ctrl + F to Search";
        Managers.TEXT.drawString(graphics, label, scaledWidth - Managers.TEXT.getWidth(label), scaledHeight - Managers.TEXT.getHeight(), ColorUtil.getSimpleVariation(0, Color.WHITE));

        if (searching)
        {
            if (completion != null)
            {
                String full = getSearch() + ChatFormatting.GRAY + completion.substring(getSearch().length());
                Managers.TEXT.drawString(graphics, full + textHandler.getIdleSign(), scaledWidth / 2f - Managers.TEXT.getWidth(full) / 2f, scaledHeight - (Managers.TEXT.getHeight() * 10), 0xFFFFFFFF);
            }
            else
            {
                Managers.TEXT.drawString(graphics, textHandler.getIdlingText(), scaledWidth / 2f - Managers.TEXT.getWidth(getSearch()) / 2f, scaledHeight - (Managers.TEXT.getHeight() * 10), 0xFFFFFFFF);
            }
        }
        else
        {
            completion = null;
            textHandler.setText("");
        }
    }

    public void onKey(int keyCode, int scanCode, int modifiers)
    {
        if (keyCode == 70 && mc.hasControlDown())
        {
            searching = !searching;
        }

        if (searching)
        {
            if (keyCode == 258 && completion != null)
            {
                textHandler.setText(completion);
            }

            if (keyCode == 257)
            {
                textHandler.setText("");
                searching = false;
            }

            if (keyCode == 256)
            {
                searching = false;
            }

            textHandler.onKey(keyCode, scanCode, modifiers);
        }

        completion = null;
    }

    public void onChar(char chr)
    {
        completion = null;
        if (searching)
        {
            textHandler.update(chr);
        }
    }
}