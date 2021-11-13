package vip.allureclient.visual.screens.dropdown.component.sub.impl;

import net.minecraft.client.gui.Gui;
import vip.allureclient.AllureClient;
import vip.allureclient.base.util.math.MathUtil;
import vip.allureclient.impl.property.ValueProperty;
import vip.allureclient.visual.screens.dropdown.component.Component;
import vip.allureclient.visual.screens.dropdown.component.sub.CheatButtonComponent;

/*
TODO: Fix sliders with new properties
 */

public class SliderComponent extends Component {

    private final ValueProperty<Double> property;
    private final CheatButtonComponent parent;
    private int offset;

    private boolean isSliding;
    private double sliderAnimation;

    public SliderComponent(ValueProperty<Double> property, CheatButtonComponent parent, int offset){
        this.property = property;
        this.parent = parent;
        this.offset = offset;
    }

    @Override
    public void onDrawScreen(int mouseX, int mouseY) {
        double x = parent.getParentFrame().getX();
        double y = parent.getParentFrame().getY() + offset;

        Gui.drawRectWithWidth(x, y, 115, 14, isMouseOverSlider(mouseX, mouseY) ? 0xff101010 : 0xff151515);

        double sliderRectPercentage = ((property.getPropertyValue() - property.getMinimumValue()) / (property.getMaximumValue() - property.getMinimumValue())) *
                (parent.getParentFrame().frameWidth - 2);
        sliderAnimation = MathUtil.animateDoubleValue(sliderRectPercentage, sliderAnimation, 0.03);

        Gui.drawHorizontalGradient((float) x + 1, (float) y + 1, Math.round(sliderAnimation), 12, 0xffd742f5, 0xff42cef5);

        AllureClient.getInstance().getFontManager().mediumFontRenderer.drawStringWithShadow(property.getPropertyLabel(), x + 3, y + 4, -1);
        AllureClient.getInstance().getFontManager().mediumFontRenderer.drawStringWithShadow(property.getPropertyValue() + "",
                x + 112 - AllureClient.getInstance().getFontManager().mediumFontRenderer.getStringWidth(property.getPropertyValue() + ""), y + 4, -1);

        if (isSliding && isMouseOverSlider(mouseX, mouseY)) {
            double mousePercent = (mouseX - (x + 1)) / (115);
            double valueToSet = MathUtil.linearInterpolate(property.getMinimumValue(), property.getMaximumValue(), mousePercent);
            property.setPropertyValue(valueToSet);
        }

        super.onDrawScreen(mouseX, mouseY);
    }

    @Override
    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isMouseOverSlider(mouseX, mouseY) && mouseButton == 0) {
            isSliding = true;
        }
    }

    @Override
    public void onMouseReleased(int mouseX, int mouseY, int mouseButton) {
        isSliding = false;
    }

    @Override
    public void setOffset(int offset) {
        this.offset = offset;
    }

    private boolean isMouseOverSlider(int mouseX, int mouseY){
        double x = parent.getParentFrame().getX();
        double y = parent.getParentFrame().getY() + offset;
        return mouseX >= x && mouseX <= x + 115
                && mouseY >= y && mouseY <= y + 14;
    }

    @Override
    public void onAnimationEvent() {
        sliderAnimation = 0;
    }

}
