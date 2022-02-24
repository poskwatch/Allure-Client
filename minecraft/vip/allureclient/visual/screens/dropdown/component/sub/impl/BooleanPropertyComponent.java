package vip.allureclient.visual.screens.dropdown.component.sub.impl;

import net.minecraft.client.gui.Gui;
import vip.allureclient.AllureClient;
import vip.allureclient.base.util.visual.AnimationUtil;
import vip.allureclient.base.util.visual.ColorUtil;
import vip.allureclient.base.util.visual.glsl.GLUtil;
import vip.allureclient.impl.property.BooleanProperty;
import vip.allureclient.visual.screens.dropdown.component.Component;
import vip.allureclient.visual.screens.dropdown.component.sub.ModuleComponent;

public class BooleanPropertyComponent extends Component {

    private final ModuleComponent parent;
    private final BooleanProperty property;
    private int offset;
    private double checkboxAnimation;
    private boolean completedAnimation;

    public BooleanPropertyComponent(BooleanProperty property, ModuleComponent parent, int offset){
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

        checkboxAnimation = AnimationUtil.linearAnimation(x + 115 - 19 + (property.getPropertyValue() ? 10 : 0), checkboxAnimation, 0.5);

        final int backgroundBox = 0xff222222;
        GLUtil.glFilledEllipse(x + 115 - 8, y + 7, 23, backgroundBox);
        GLUtil.glFilledEllipse(x + 115 - 20, y + 7, 23, backgroundBox);
        GLUtil.drawFilledRectangle(x + 115 - 20, y + 1.5, 13, 11, backgroundBox);

        GLUtil.glFilledEllipse(checkboxAnimation, y + 7, 20, property.getPropertyValue() ? ColorUtil.getClientColors()[1].getRGB() : 0xffe1e1e1);

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
