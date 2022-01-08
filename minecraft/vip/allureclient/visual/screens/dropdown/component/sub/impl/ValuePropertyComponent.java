package vip.allureclient.visual.screens.dropdown.component.sub.impl;

import vip.allureclient.AllureClient;
import vip.allureclient.base.util.math.MathUtil;
import vip.allureclient.base.util.visual.AnimationUtil;
import vip.allureclient.base.util.visual.ColorUtil;
import vip.allureclient.base.util.visual.GLUtil;
import vip.allureclient.impl.property.ValueProperty;
import vip.allureclient.visual.screens.dropdown.component.Component;
import vip.allureclient.visual.screens.dropdown.component.sub.ModuleComponent;


public class ValuePropertyComponent extends Component {

    @SuppressWarnings("rawtypes")
    private final ValueProperty property;
    private final ModuleComponent parent;
    private int offset;

    private boolean isSliding;
    private double sliderAnimation;

    @SuppressWarnings("rawtypes")
    public ValuePropertyComponent(ValueProperty property, ModuleComponent parent, int offset){
        this.property = property;
        this.parent = parent;
        this.offset = offset;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onDrawScreen(int mouseX, int mouseY) {
        double x = parent.getParentFrame().getX();
        double y = parent.getParentFrame().getY() + offset;

        GLUtil.glFilledQuad(x, y, 115, 23, isMouseOverSlider(mouseX, mouseY) ? 0xff101010 : 0xff151515);

        if (isSliding && isMouseOverSlider(mouseX, mouseY)) {
            double max = property.getMaximumValue().doubleValue();
            double min = property.getMinimumValue().doubleValue();
            double mousePercent = (mouseX - (x + 5)) / (106);
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

        double sliderRectPercentage = (106 *
                ((property.getPropertyValue().doubleValue() - property.getMinimumValue().doubleValue())
                        / (property.getMaximumValue().doubleValue() - property.getMinimumValue().doubleValue())));

        sliderAnimation = AnimationUtil.linearAnimation(sliderRectPercentage, sliderAnimation, 3);

        GLUtil.glHorizontalGradientQuad(x + 2, y + 17, 111, 1, ColorUtil.getClientColors()[0].getRGB(), ColorUtil.getClientColors()[1].getRGB());
        GLUtil.glFilledEllipse(x + 5 + sliderAnimation, y + 17, 15, -1);

        String currentValue = null;
        if (property.getPropertyValue() instanceof Integer || property.getPropertyValue() instanceof Long) {
            currentValue = property.getPropertyValue().toString();
        } else if (property.getPropertyValue() instanceof Float || property.getPropertyValue() instanceof Double) {
            currentValue = String.format("%.2f", property.getPropertyValue().doubleValue());
        }

        getFontRenderer().drawStringWithShadow(property.getPropertyLabel(), x + 3, y + 4, -1);
        getFontRenderer().drawStringWithShadow(currentValue, x + 112 - AllureClient.getInstance().getFontManager().smallFontRenderer.getStringWidth(currentValue), y + 4, 0xff999999);

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
        return mouseX >= x + 5 && mouseX <= x + 111
                && mouseY >= y && mouseY <= y + 22;
    }

    @Override
    public boolean isHidden() {
        return getProperty().isPropertyHidden();
    }

    @SuppressWarnings("rawtypes")
    public ValueProperty getProperty() {
        return property;
    }

    @Override
    public void onGuiClosed() {
        isSliding = false;
        super.onGuiClosed();
    }

    @Override
    public double getHeight() {
        return 23;
    }

    @Override
    public void onAnimationEvent() {
        sliderAnimation = 0;
    }

}
