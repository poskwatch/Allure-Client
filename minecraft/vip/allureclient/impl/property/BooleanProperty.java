package vip.allureclient.impl.property;

import vip.allureclient.base.module.Module;
import vip.allureclient.base.property.Property;

public class BooleanProperty extends Property<Boolean> {

    public BooleanProperty(String propertyLabel, Boolean propertyValue, Module parentModule) {
        super(propertyLabel, propertyValue, parentModule);
    }

    @Override
    public Boolean getPropertyValue() {
        return super.getPropertyValue();
    }

    @Override
    public void setPropertyValue(Boolean propertyValue) {
        super.setPropertyValue(propertyValue);
        onValueChange.run();
    }
}
