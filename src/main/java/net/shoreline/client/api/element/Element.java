package net.shoreline.client.api.element;

import com.mojang.blaze3d.platform.Window;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.shoreline.client.api.gui.handler.DragHandler;
import net.shoreline.client.api.module.Category;
import net.shoreline.client.api.module.Toggleable;
import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.api.setting.impl.EnumSetting;
import net.shoreline.client.api.setting.impl.NumberSetting;
import net.shoreline.client.impl.Managers;
import net.shoreline.client.impl.modules.client.HudModule;
import net.shoreline.client.impl.modules.client.ThemeModule;
import net.shoreline.client.impl.render.Render2DUtil;

public abstract class Element extends Toggleable
{
    Setting<Float> xPos = new NumberSetting.Builder<Float>("X-Pos")
            .setMin(-10000f).setMax(10000f).setDefaultValue(0f)
            .setVisible(() -> false).build();
    Setting<Float> yPos = new NumberSetting.Builder<Float>("Y-Pos")
            .setMin(-10000f).setMax(10000f).setDefaultValue(0f)
            .setVisible(() -> false).build();
    Setting<Anchor> anchor = new EnumSetting.Builder<Anchor>("Anchor")
            .setDefaultValue(Anchor.NONE)
            .setVisible(() -> false)
            .build();
    Setting<Integer> index = new NumberSetting.Builder<Integer>("Index")
            .setMin(0).setMax(Integer.MAX_VALUE).setDefaultValue(0)
            .setVisible(() -> false).build();

    @Getter
    private final DragHandler drag;

    public Element(String name, String description, float x, float y)
    {
        this(name, new String[]{}, description, x, y);
    }

    public Element(String name, String[] nameAliases, String description, float x, float y)
    {
        super(name, nameAliases, description, Category.HUD);
        drag = new DragHandler(this::getX, this::getY, this::setX, this::setY);
        xPos.setValue(x);
        yPos.setValue(y);
        register(xPos, yPos, anchor, index);
    }

    public abstract void draw(GuiGraphicsExtractor graphics, float partialTicks);

    public abstract float getWidth();

    public abstract float getHeight();

    public void drawGui(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks)
    {
        drag.handleRender(mouseX, mouseY);
        if (drag.isDragging())
        {
            updateAnchor();
            checkHovered();
        }

        Render2DUtil.drawBorderedRect(graphics, getX(), getY(), getX() + getWidth(), getY() + getHeight(), 0.5f, 0x60000000, drag.isDragging() ? 0xFFFFFFFF : 0);
        draw(graphics, partialTicks);
    }

    public void mouseClicked(float mouseX, float mouseY, int button)
    {
        boolean hovered = mouseWithinBounds(mouseX, mouseY, getX(), getY(), getWidth(), getHeight());
        drag.handleMouseClicked(mouseX, mouseY, button, hovered);
    }

    public void mouseReleased()
    {
        drag.setDragging(false);
    }

    public void setX(float x)
    {
        Window resolution = mc.getWindow();
        float sW = resolution.getGuiScaledWidth();
        float padding = HudModule.INSTANCE.getPadding().getValue();
        float clampedX = Math.clamp(x, padding, sW - getWidth() - padding);
        this.xPos.setValue(clampedX);
    }

    public void setY(float y)
    {
        Window resolution = mc.getWindow();
        float sH = resolution.getGuiScaledHeight();
        float padding = HudModule.INSTANCE.getPadding().getValue();
        float clampedY = Math.clamp(y, padding, sH - padding - getHeight());
        yPos.setValue(clampedY);
    }

    public float getX()
    {
        return xPos.getValue();
    }

    public float getY()
    {
        return yPos.getValue();
    }

    public Anchor getAnchor()
    {
        return anchor.getValue();
    }

    public void setAnchor(Anchor anchor)
    {
        this.anchor.setValue(anchor);
    }

    public int getIndex()
    {
        return index.getValue();
    }

    public void setIndex(int index)
    {
        this.index.setValue(index);
    }

    public void drawText(GuiGraphicsExtractor graphics, String text, float x, float y)
    {
        Managers.TEXT.drawString(graphics, text, x, y, ThemeModule.INSTANCE.getPrimary());
    }

    public void updateAnchor()
    {
        Window resolution = mc.getWindow();
        float offset = 10;

        float x = getX();
        float y = getY();
        float width = getWidth();
        float height = getHeight();

        float screenWidth = resolution.getGuiScaledWidth();
        float screenHeight = resolution.getGuiScaledHeight();

        boolean nearLeft = x <= offset;
        boolean nearRight = x >= screenWidth - width - offset;
        boolean nearTop = y <= offset;
        boolean nearBottom = y >= screenHeight - height - offset;

        float centerX = screenWidth / 2.0f;
        boolean nearTopMiddle = nearTop
                && !nearLeft
                && !nearRight
                && Math.abs((x + width / 2.0f) - centerX) <= offset;

        Anchor found;
        if (nearLeft && nearTop)
        {
            found = Anchor.TOP_LEFT;
        }
        else if (nearRight && nearTop)
        {
            found = Anchor.TOP_RIGHT;
        }
        else if (nearLeft && nearBottom)
        {
            found = Anchor.BOTTOM_LEFT;
        }
        else if (nearRight && nearBottom)
        {
            found = Anchor.BOTTOM_RIGHT;
        }
        else if (nearTopMiddle)
        {
            found = Anchor.MIDDLE;
        }
        else
        {
            setIndex(0);
            setAnchor(Anchor.NONE);
            return;
        }

        setIndex(Integer.MAX_VALUE);
        setAnchor(found);
    }

    public boolean checkHovered()
    {
        for (Element element : Managers.MODULES.getElements())
        {
            if (element.getAnchor() == Anchor.NONE)
            {
                continue;
            }

            float ex = element.getX();
            float ey = element.getY();
            float ew = element.getWidth();
            float eh = element.getHeight();
            boolean overlaps = getX() + getWidth() >= ex &&
                    getX() <= ex + ew &&
                    getY() + getHeight() >= ey &&
                    getY() <= ey + eh;

            if (overlaps)
            {
                setIndex(element.getIndex() + 1);
                setAnchor(element.getAnchor());
                return false;
            }
        }

        return true;
    }

    public boolean mouseWithinBounds(double mouseX,
                                     double mouseY,
                                     double x,
                                     double y,
                                     double width,
                                     double height)
    {
        return (mouseX >= x && mouseX <= (x + width)) &&
                (mouseY >= y && mouseY <= (y + height));
    }
}
