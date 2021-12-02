package vip.allureclient.visual.screens.dropdown.component.sub.impl;

import net.minecraft.client.gui.Gui;
import vip.allureclient.AllureClient;
import vip.allureclient.base.util.math.MathUtil;
import vip.allureclient.impl.property.BooleanProperty;
import vip.allureclient.visual.screens.dropdown.component.Component;
import vip.allureclient.visual.screens.dropdown.component.sub.CheatButtonComponent;

public class BooleanPropertyComponent extends Component {

    private final CheatButtonComponent parent;
    private final BooleanProperty property;
    private int offset;
    private double checkboxAnimation;
    private boolean completedAnimation;

    public BooleanPropertyComponent(BooleanProperty property, CheatButtonComponent parent, int offset){
        this.parent = parent;
        this.property = property;
        this.offset = offset;
    }

    @Override
    public void onDrawScreen(int mouseX, int mouseY) {
        double x = parent.getParentFrame().getX();
        double y = parent.getParentFrame().getY() + offset;

        Gui.drawRectWithWidth(x, y, 115, 14, isMouseHovering(mouseX, mouseY) ? 0xff101010 : 0xff151515);

        AllureClient.getInstance().getFontManager().smallFontRenderer.drawStringWithShadow(property.getPropertyLabel(), x + 3, y + 4, -1);
        Gui.drawRectWithWidth(x + 115 - 28, y + 2, 26, 10, 0x99000000);

        checkboxAnimation = MathUtil.animateDoubleValue(x + 115 - 27 + (property.getPropertyValue() ? 12 : 0), checkboxAnimation, 0.03);

        Gui.drawRectWithWidth(completedAnimation ? x + 115 - 27 + (property.getPropertyValue() ? 12 : 0) : Math.round(checkboxAnimation), y + 3, 12, 8,
                property.getPropertyValue() ? 0xff42cef5 : 0xff202020);

        if(Math.round(checkboxAnimation) == x + 115 - 27 + (property.getPropertyValue() ? 12 : 0)){
            completedAnimation = true;
        }
    }

    @Override
    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(isMouseHovering(mouseX, mouseY)){
            property.setPropertyValue(!property.getPropertyValue());
            completedAnimation = false;
        }
    }

    @Override
    public void setOffset(int offset) {
        this.offset = offset;
    }

    private boolean isMouseHovering(int mouseX, int mouseY){
        double x = parent.getParentFrame().getX();
        double y = parent.getParentFrame().getY() + offset;
        return mouseX >= x && mouseX <= x + 115 && mouseY >= y && mouseY <= y + 14;
    }

    public BooleanProperty getProperty() {
        return property;
    }

    @Override
    public boolean isHidden() {
        return getProperty().isPropertyHidden();
    }

    @Override
    public void onAnimationEvent() {
        checkboxAnimation = parent.getParentFrame().getX() + 115 - 27;
    }
}
