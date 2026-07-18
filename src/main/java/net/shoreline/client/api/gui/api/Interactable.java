package net.shoreline.client.api.gui.api;

public interface Interactable
{
    void mouseClicked(double mouseX,
                      double mouseY,
                      int button);

    void mouseReleased(double mouseX,
                      double mouseY,
                      int button);

    void mouseScrolled(double x,
                       double y,
                       double scrollX,
                       double scrollY);

    void keyTyped(int key,
                  int scancode,
                  int modifiers);

    void charTyped(char chr);
}