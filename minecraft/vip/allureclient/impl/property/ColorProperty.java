package vip.allureclient.impl.property;

import vip.allureclient.base.module.Module;
import vip.allureclient.base.property.Property;

import java.awt.*;

public class ColorProperty extends Property<Color> {

    public ColorProperty(String propertyLabel, Color propertyValue, Module parentModule) {
        super(propertyLabel, propertyValue, parentModule);
    }

    @Override
    public Color getPropertyValue() {
        return super.getPropertyValue();
    }

    public int getPropertyValueRGB() {
        return getPropertyValue().getRGB();
    }

    @Override
    public void setPropertyValue(Color propertyValue) {
        super.setPropertyValue(propertyValue);
    }

}
