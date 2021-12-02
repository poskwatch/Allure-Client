package vip.allureclient.impl.property;

import vip.allureclient.base.module.Module;
import vip.allureclient.base.property.Property;

public class ValueProperty<T extends Number> extends Property<T> {

    private final T minimumValue;
    private final T maximumValue;

    public ValueProperty(String propertyLabel, T propertyValue, T minimumValue, T maximumValue, Module parentModule) {
        super(propertyLabel, propertyValue, parentModule);
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
    }

    @Override
    public T getPropertyValue() {
        return super.getPropertyValue();
    }

    @Override
    public void setPropertyValue(T propertyValue) {
        super.setPropertyValue(propertyValue);
        onValueChange.run();
    }

    public T getMinimumValue() {
        return minimumValue;
    }

    public T getMaximumValue() {
        return maximumValue;
    }
}
