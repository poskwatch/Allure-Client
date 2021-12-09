package vip.allureclient.visual.screens.dropdown.component.sub.impl;

import net.minecraft.client.gui.Gui;
import vip.allureclient.impl.property.MultiSelectEnumProperty;
import vip.allureclient.visual.screens.dropdown.component.Component;

public class MultiSelectEnumPropertyComponent extends Component {

    private int offset;
    @SuppressWarnings("rawtypes")
    private final MultiSelectEnumProperty property;
    private final Component parent;
    private boolean expanded;

    public MultiSelectEnumPropertyComponent(MultiSelectEnumProperty<?> property, Component parent, int offset){
        this.offset = offset;
        this.property = property;
        this.parent = parent;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onDrawScreen(int mouseX, int mouseY) {
        double x = parent.getParentFrame().getX();
        double y = parent.getParentFrame().getY() + offset;

        Gui.drawRectWithWidth(x, y, 115, 14, isMouseHovering(mouseX, mouseY) ? 0xff101010 : 0xff151515);

        fontRenderer.drawStringWithShadow(property.getPropertyLabel(), x + 3, y + 4, -1);

        fontRenderer.drawStringWithShadow(property.getSelectedValues().size() + " Selected",
                x + 112 - fontRenderer.getStringWidth(property.getSelectedValues().size() + " Selected"), y + 4, 0xff999999);

        if (expanded) {
            Gui.drawRectWithWidth(x + 5, y + 15, 105, property.getConstantEnumValues().length * 15, 0xff202020);
            for (int i = 0; i < property.getConstantEnumValues().length; i++) {
                boolean hoveringText = mouseX >= x + 5 && mouseX <= x + 110
                        && mouseY >= (y + 19 + (i * 15)) && mouseY <= (y + 19 + (i * 15) + 13);
                fontRenderer.drawCenteredStringWithShadow(property.getConstantEnumValues()[i].toString(), (float) x + 57.5F, (float) (y + 19 + i * 15),
                        property.isSelected(property.getConstantEnumValues()[i]) ? -1 : hoveringText ? 0xff999999 : 0xff707070);
            }
        }

        super.onDrawScreen(mouseX, mouseY);
    }

    private boolean isMouseHovering(int mouseX, int mouseY){
        double x = parent.getParentFrame().getX();
        double y = parent.getParentFrame().getY() + offset;

        return mouseX >= x && mouseX <= x + 115 && mouseY >= y && mouseY <= y + 14;
    }

    @Override
    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 1 && isMouseHovering(mouseX, mouseY)) {
            this.expanded = !expanded;
        }

        double x = parent.getParentFrame().getX();
        double y = parent.getParentFrame().getY() + offset;

        if (mouseButton == 0 && expanded) {
            for (int i = 0; i < property.getConstantEnumValues().length; i++) {
                boolean hoveringText = mouseX >= x + 5 && mouseX <= x + 110
                        && mouseY >= (y + 19 + (i * 15)) && mouseY <= (y + 19 + (i * 15) + 13);
                if (hoveringText) {
                    property.setValue(i);
                }
            }
        }
    }

    @Override
    public double getHeight() {
        return expanded ? 17 + property.getConstantEnumValues().length * 15 : super.getHeight();
    }

    @Override
    public void setOffset(int offset) {
        this.offset = offset;
    }
}
