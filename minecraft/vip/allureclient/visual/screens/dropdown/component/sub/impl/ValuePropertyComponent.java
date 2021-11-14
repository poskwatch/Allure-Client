package vip.allureclient.visual.screens.dropdown.component.sub.impl;

import net.minecraft.client.gui.Gui;
import vip.allureclient.AllureClient;
import vip.allureclient.base.util.math.MathUtil;
import vip.allureclient.impl.property.ValueProperty;
import vip.allureclient.visual.screens.dropdown.component.Component;
import vip.allureclient.visual.screens.dropdown.component.sub.CheatButtonComponent;


public class ValuePropertyComponent extends Component {

    private final ValueProperty property;
    private final CheatButtonComponent parent;
    private int offset;

    private boolean isSliding;
    private double sliderAnimation;

    public ValuePropertyComponent(ValueProperty property, CheatButtonComponent parent, int offset){
        this.property = property;
        this.parent = parent;
        this.offset = offset;
    }

    @Override
    public void onDrawScreen(int mouseX, int mouseY) {
        double x = parent.getParentFrame().getX();
        double y = parent.getParentFrame().getY() + offset;

        Gui.drawRectWithWidth(x, y, 115, 14, isMouseOverSlider(mouseX, mouseY) ? 0xff101010 : 0xff151515);


        if (isSliding && isMouseOverSlider(mouseX, mouseY)) {
            double max = property.getMaximumValue().doubleValue();
            double min = property.getMinimumValue().doubleValue();
            double mousePercent = (mouseX - (x)) / (115);
            double valueToSet = MathUtil.linearInterpolate(min, max, mousePercent);
            if (property.getPropertyValue() instanceof Double) {
                property.setPropertyValue(MathUtil.roundToPlace(valueToSet, 2));
            }
            if (property.getPropertyValue() instanceof Integer) {
                property.setPropertyValue((int) MathUtil.roundToPlace(valueToSet, 2));
            }
            if (property.getPropertyValue() instanceof Float) {
                property.setPropertyValue((float) MathUtil.roundToPlace(valueToSet, 2));
            }
            if(property.getPropertyValue() instanceof Long){
                property.setPropertyValue((long) MathUtil.roundToPlace(valueToSet, 2));
            }
            if(property.getPropertyValue() instanceof Byte){
                property.setPropertyValue((byte) MathUtil.roundToPlace(valueToSet, 2));
            }
        }

        double sliderRectPercentage = ((property.getPropertyValue().doubleValue() - property.getMinimumValue().doubleValue()) / (property.getMaximumValue().doubleValue() - property.getMinimumValue().doubleValue())) *
                (parent.getParentFrame().frameWidth - 2);

        sliderAnimation = MathUtil.animateDoubleValue(sliderRectPercentage, sliderAnimation, 0.03);

        Gui.drawHorizontalGradient((float) x + 1, (float) y + 1, Math.round(sliderAnimation), 12, 0xffd742f5, 0xff42cef5);

        String currentValue = null;
        if (property.getPropertyValue() instanceof Integer || property.getPropertyValue() instanceof Long) {
            currentValue = property.getPropertyValue().toString();
        } else if (property.getPropertyValue() instanceof Float || property.getPropertyValue() instanceof Double) {
            currentValue = String.format("%.2f", property.getPropertyValue().doubleValue());
        }

        AllureClient.getInstance().getFontManager().smallFontRenderer.drawStringWithShadow(property.getPropertyLabel(), x + 3, y + 4, -1);
        AllureClient.getInstance().getFontManager().smallFontRenderer.drawStringWithShadow(currentValue,
                x + 112 - AllureClient.getInstance().getFontManager().smallFontRenderer.getStringWidth(currentValue), y + 4, -1);

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
