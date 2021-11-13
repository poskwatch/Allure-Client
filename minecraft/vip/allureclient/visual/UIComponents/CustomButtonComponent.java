package vip.allureclient.visual.UIComponents;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class CustomButtonComponent extends GuiButton {

    public CustomButtonComponent(int buttonId, int x, int y, String buttonText) {
        super(buttonId, x, y, buttonText);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        super.drawButton(mc, mouseX, mouseY);
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        return super.mousePressed(mc, mouseX, mouseY);
    }

    @Override
    public boolean isMouseOver() {
        return super.isMouseOver();
    }


}
