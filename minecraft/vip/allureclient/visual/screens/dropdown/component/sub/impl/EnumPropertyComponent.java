package vip.allureclient.visual.screens.dropdown.component.sub.impl;

import net.minecraft.client.gui.Gui;
import vip.allureclient.impl.property.EnumProperty;
import vip.allureclient.visual.screens.dropdown.component.Component;


public class EnumPropertyComponent extends Component {

    private int offset;
    @SuppressWarnings("rawtypes")
    private final EnumProperty property;
    private final Component parent;
    private boolean expanded;

    public EnumPropertyComponent(EnumProperty<?> property, Component parent, int offset){
        this.offset = offset;
        this.property = property;
        this.parent = parent;
    }

    @Override
    public void onDrawScreen(int mouseX, int mouseY) {
        double x = parent.getParentFrame().getX();
        double y = parent.getParentFrame().getY() + offset;

        Gui.drawRectWithWidth(x, y, 115, 14, isMouseHovering(mouseX, mouseY) ? 0xff101010 : 0xff151515);

        fontRenderer.drawStringWithShadow(property.getPropertyLabel(), x + 3, y + 4, -1);
        fontRenderer.drawStringWithShadow(property.getEnumValueAsString(),
                x + 115 - fontRenderer.getStringWidth(property.getEnumValueAsString()) - 3, y + 4, 0xff999999);

        if (expanded) {
            Gui.drawRectWithWidth(x + 5, y + 15, 105, property.getPropertyEnumConstants().length * 15, 0xff202020);
            for (int i = 0; i < property.getPropertyEnumConstants().length; i++) {
                boolean hoveringText = mouseX >= x + 5 && mouseX <= x + 110
                        && mouseY >= (y + 19 + (i * 15)) && mouseY <= (y + 19 + (i * 15) + 13);
                fontRenderer.drawCenteredStringWithShadow(
                        property.getPropertyEnumConstants()[i].toString(), (float) x + 57.5F, (float) (y + 19 + (i * 15)),
                        property.getPropertyValue() == property.getPropertyEnumConstants()[i] ? -1 : hoveringText ? 0xff999999 : 0xff707070);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        double x = parent.getParentFrame().getX();
        double y = parent.getParentFrame().getY() + offset;
        if(isMouseHovering(mouseX, mouseY)) {
            if (mouseButton == 0) {
                property.increment();
            }
            if(mouseButton == 1){
                expanded = !expanded;
            }
        }
        if (expanded) {
            for (int i = 0; i < property.getPropertyEnumConstants().length; i++) {
                boolean hoveringText = mouseX >= x + 5 && mouseX <= x + 110
                        && mouseY >= (y + 19 + (i * 15)) && mouseY <= (y + 19 + (i * 15) + 13);
                if (hoveringText) {
                    property.setPropertyValue(property.getPropertyEnumConstants()[i]);
                }
            }
        }
    }

    @Override
    public double getHeight() {
        if (expanded) {
            return 17 + property.getPropertyEnumConstants().length * 15;
        }
        else
            return super.getHeight();
    }

    private boolean isMouseHovering(int mouseX, int mouseY){
        double x = parent.getParentFrame().getX();
        double y = parent.getParentFrame().getY() + offset;

        return mouseX >= x && mouseX <= x + 115 && mouseY >= y && mouseY <= y + 14;
    }

    @Override
    public boolean isHidden() {
        return getProperty().isPropertyHidden();
    }

    @Override
    public void setOffset(int offset) {
        this.offset = offset;
    }

    public EnumProperty<?> getProperty() {
        return property;
    }
}
