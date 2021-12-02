package vip.allureclient.impl.property;

import vip.allureclient.base.module.Module;
import vip.allureclient.base.property.Property;

import java.util.Arrays;

public class EnumProperty<T extends Enum<T>> extends Property<T> {

    private final T[] propertyEnumConstants;

    public EnumProperty(String propertyLabel, T propertyValue, Module parentModule) {
        super(propertyLabel, propertyValue, parentModule);
        this.propertyEnumConstants = propertyValue.getDeclaringClass().getEnumConstants();
    }

    public T[] getPropertyEnumConstants() {
        return propertyEnumConstants;
    }

    public String getEnumValueAsString() {
        return getPropertyValue().toString();
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

    public void increment() {
        T currentValue = getPropertyValue();
        for (T constant : getPropertyEnumConstants()) {
            if (constant != currentValue) {
                continue;
            }
            T newValue;
            int ordinal = Arrays.asList(getPropertyEnumConstants()).indexOf(constant);
            if (ordinal == getPropertyEnumConstants().length - 1) {
                newValue = getPropertyEnumConstants()[0];
            } else {
                newValue = getPropertyEnumConstants()[ordinal + 1];
            }
            setPropertyValue(newValue);
            return;
        }
        onValueChange.run();
    }

    public void decrement() {
        T currentValue = getPropertyValue();
        for (T constant : getPropertyEnumConstants()) {
            if (constant != currentValue) {
                continue;
            }
            T newValue;
            int ordinal = Arrays.asList(getPropertyEnumConstants()).indexOf(constant);
            if (ordinal == 0) {
                newValue = getPropertyEnumConstants()[getPropertyEnumConstants().length - 1];
            } else {
                newValue = getPropertyEnumConstants()[ordinal - 1];
            }
            setPropertyValue(newValue);
            return;
        }
        onValueChange.run();
    }
}
