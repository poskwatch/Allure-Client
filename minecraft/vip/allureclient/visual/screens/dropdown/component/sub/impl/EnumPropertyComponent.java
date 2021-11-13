package vip.allureclient.visual.screens.dropdown.component.sub.impl;

import net.minecraft.client.gui.Gui;
import vip.allureclient.AllureClient;
import vip.allureclient.impl.property.EnumProperty;
import vip.allureclient.visual.screens.dropdown.component.Component;

/**
 * @Author: Posk
 * @Date: 11/6/2021
 **/

public class EnumPropertyComponent extends Component {

    private int offset;
    private EnumProperty property;
    private Component parent;

    public EnumPropertyComponent(EnumProperty property, Component parent, int offset){
        this.offset = offset;
        this.property = property;
        this.parent = parent;
    }

    @Override
    public void onDrawScreen(int mouseX, int mouseY) {
        double x = parent.getParentFrame().getX();
        double y = parent.getParentFrame().getY() + offset;

        Gui.drawRectWithWidth(x, y, 115, 14, isMouseHovering(mouseX, mouseY) ? 0xff101010 : 0xff151515);

        AllureClient.getInstance().getFontManager().mediumFontRenderer.drawStringWithShadow(property.getPropertyLabel(), x + 3, y + 4, -1);
        AllureClient.getInstance().getFontManager().mediumFontRenderer.drawStringWithShadow(property.getEnumValueAsString(),
                x + 115 - AllureClient.getInstance().getFontManager().mediumFontRenderer.getStringWidth(property.getEnumValueAsString()) - 3, y + 4, -1);
    }

    @Override
    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(isMouseHovering(mouseX, mouseY)) {
            if (mouseButton == 0) {
                property.increment();
            }
            if(mouseButton == 1){
                property.decrement();
            }
        }
    }

    private boolean isMouseHovering(int mouseX, int mouseY){
        double x = parent.getParentFrame().getX();
        double y = parent.getParentFrame().getY() + offset;

        return mouseX >= x && mouseX <= x + 115 && mouseY >= y && mouseY <= y + 14;
    }

    @Override
    public void setOffset(int offset) {
        this.offset = offset;
    }
}