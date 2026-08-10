package net.shoreline.client.api.gui.component;

import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.shoreline.client.api.gui.ShorelineGui;
import net.shoreline.client.api.gui.api.Interactable;
import net.shoreline.client.api.gui.handler.SearchHandler;
import net.shoreline.client.api.setting.impl.RegistrySetting;
import net.shoreline.client.impl.Managers;
import net.shoreline.client.impl.render.ColorUtil;
import net.shoreline.client.impl.render.Render2DUtil;
import net.shoreline.client.impl.render.animation.ColorAnimation;
import net.shoreline.client.impl.render.animation.Easing;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

//TODO: the visibility animations for this is pretty funky.
@SuppressWarnings("unchecked")
public class RegistrySelectionComponent<T> extends GridParentComponent
{
    private final RegistrySetting<T> setting;
    private final MutableRegistryEntry<T>[] entries = new MutableRegistryEntry[12];
    private final SearchHandler searchHandler = new SearchHandler();

    public RegistrySelectionComponent(RegistrySetting<T> setting)
    {
        super("Selection", () -> true, 4);
        this.setting = setting;

        for (int i = 0; i < entries.length; i++)
        {
            MutableRegistryEntry<T> entry = new MutableRegistryEntry<>(setting);
            entries[i] = entry;
            components.add(entry);
        }

        rebuild();
    }

    @Override
    public void drawComponent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks)
    {
        super.drawComponent(graphics, mouseX, mouseY, partialTicks);
        if (open && animation.getFactor() > 0.5)
        {
            String currentSearch = searchHandler.getSearch();
            String searchState = searchHandler.isSearching()
                    ? searchHandler.getCompletion() != null ? currentSearch + ChatFormatting.GRAY + searchHandler.getCompletion().substring(currentSearch.length()) : currentSearch
                    : ChatFormatting.GRAY + "Press to search...";

            drawString(graphics, searchState, getX() + getTextPadding() + 1, getY() + getActualHeight() - Managers.TEXT.getHeight() + 4.25f, false, false);
        }
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks)
    {
        super.render(graphics, mouseX, mouseY, deltaTicks);

        if (open)
        {
            rebuild();
            for (MutableRegistryEntry<T> component : entries)
            {
                if (component.getEntry() != null)
                {
                    boolean in = setting.getValue().contains(component.getEntry());
                    component.getColorAnimation().setState(in);
                }
            }
        }
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button)
    {
        super.mouseClicked(mouseX, mouseY, button);
        if (animation.getFactor() > 0.99 && mouseWithinBounds(mouseX, mouseY, getAlignedX(), getY() + getActualHeight() - 11, getWidth(), 11))
        {
            searchHandler.setSearching(!searchHandler.isSearching());
            searchHandler.getTextHandler().setText("");
        }
    }

    @Override
    public void keyTyped(int key, int scancode, int modifiers)
    {
        super.keyTyped(key, scancode, modifiers);
        if (open && key != 70)
        {
            searchHandler.onKey(key, scancode, modifiers);
        }
    }

    @Override
    public void charTyped(char chr)
    {
        super.charTyped(chr);
        if (open)
        {
            searchHandler.onChar(chr);
        }
    }

    public void rebuild()
    {
        List<T> found = findEntries();
        for (int i = 0; i < 12; i++)
        {
            MutableRegistryEntry<T> component = entries[i];
            if (i < found.size())
            {
                T entry = found.get(i);
                component.setEntry(entry, getName(entry));
            }
            else
            {
                component.setEntry(null, null);
            }
        }
    }

    private List<T> findEntries()
    {
        List<T> result = new ArrayList<>(12);
        for (T entry : setting.getRegistry())
        {
            if (entry == Blocks.AIR || entry == Items.AIR)
            {
                continue;
            }

            Identifier id = setting.getRegistry().getKey(entry);
            if (id == null)
            {
                continue;
            }

            boolean searching = searchHandler.isSearching();
            String search = searching ? searchHandler.getSearch().toLowerCase() : "";
            String name   = getName(entry).toLowerCase();
            if (!searchHandler.isSearching() || name.startsWith(search))
            {
                result.add(entry);
                if (result.size() >= 12)
                {
                    break;
                }
            }
        }

        return result;
    }

    public String getName(T entry)
    {
        if (entry instanceof Block block)
        {
            return I18n.get(block.getDescriptionId());
        }
        else
        {
            ItemStack stack = toStack(entry);
            return stack.getItemName().getString();
        }
    }

    private ItemStack toStack(T entry)
    {
        if (entry instanceof Item it)
        {
            return new ItemStack(it);
        }
        else if (entry instanceof Block b)
        {
            return new ItemStack(b.asItem());
        }

        return ItemStack.EMPTY;
    }

    // make space for search text.
    @Override
    public float getPadding()
    {
        return 12f;
    }

    @Override
    public boolean checkComponent(AbstractComponent component)
    {
        if (searchHandler.isSearching())
        {
            if (!component.getLabel().toLowerCase().startsWith(searchHandler.getSearch().toLowerCase()))
            {
                return false;
            }

            if (!searchHandler.getSearch().isEmpty())
            {
                searchHandler.setCompletion(component.getLabel());
            }
        }

        return super.checkComponent(component);
    }

    @Getter
    public static class MutableRegistryEntry<T> extends AbstractComponent implements Interactable
    {
        private final ColorAnimation colorAnimation;
        private final Supplier<Boolean> supplier;
        private T entry;
        private String entryName;
        private boolean selected;

        public MutableRegistryEntry(RegistrySetting<T> setting)
        {
            super("Component", () -> true);
            this.colorAnimation = new ColorAnimation(150, Easing.LINEAR);
            this.supplier = () ->
            {
                if (entry == null)
                {
                    return false;
                }

                if (setting.getValue().contains(entry))
                {
                    setting.remove(entry);
                    return false;
                }
                else
                {
                    setting.add(entry);
                    return true;
                }
            };
        }

        @Override
        public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks)
        {
            super.render(graphics, mouseX, mouseY, partialTicks);
            setHeight(getWidth());
        }

        @Override
        public void drawComponent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks)
        {
            if (entry == null)
            {
                return;
            }

            double hFactor = hoverAnimation.getFactor();
            double eFactor = colorAnimation.getFactor();

            Color hoverColor = ColorUtil.withTransparency(
                    Color.GRAY,
                    Math.max(50, (int) (75 * hFactor))
            );

            Color clr = getTheme().getPrimaryC(0.5f);
            Color enabledColor = ColorUtil.interpolate(clr, clr.brighter(), hFactor);

            Color color = ColorUtil.interpolate(
                    hoverColor,
                    enabledColor,
                    eFactor
            );

            Render2DUtil.drawRect(graphics, getX(), getY() + 1.5f, getX() + getWidth(), getY() + getFeatureHeight(), color.getRGB());
            drawEntry(graphics, entry, (int) (getX() + (getWidth() / 2f) - 8), (int) (getY() + (getHeight() / 2f)) - 6);
        }

        @Override
        public boolean isVisible()
        {
            return entry != null && entryName != null;
        }

        @Override
        public String getLabel()
        {
            return Objects.requireNonNullElse(entryName, "");
        }

        @Override
        public float getFeatureHeight()
        {
            return getWidth();
        }

        public void setEntry(T entry, String name)
        {
            this.entry = entry;
            this.entryName = name;
        }

        public void drawEntry(GuiGraphicsExtractor graphics, T entry, int x, int y)
        {
            ItemStack stack = toStack(entry);
            if (!stack.isEmpty())
            {
                graphics.item(stack, x, y);
            }
        }

        private ItemStack toStack(T entry)
        {
            if (entry instanceof Item it)
            {
                return new ItemStack(it);
            }
            else if (entry instanceof Block b)
            {
                return new ItemStack(b.asItem());
            }

            return ItemStack.EMPTY;
        }

        @Override
        public void mouseClicked(double mouseX, double mouseY, int button)
        {
            if (isHovered(mouseX, mouseY))
            {
                colorAnimation.setState(supplier.get());
            }
        }

        @Override
        public void mouseReleased(double mouseX, double mouseY, int button)
        {

        }

        @Override
        public void mouseScrolled(double x, double y, double scrollX, double scrollY)
        {

        }

        @Override
        public void keyTyped(int key, int scancode, int modifiers)
        {

        }

        @Override
        public void charTyped(char chr)
        {

        }
    }
}
