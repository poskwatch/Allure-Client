package vip.allureclient.impl.property;

import vip.allureclient.base.module.Module;
import vip.allureclient.base.property.Property;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MultiSelectEnumProperty<T extends Enum<T>> extends Property<List<T>> {

    private final T[] constantEnumValues;

    @SafeVarargs
    public MultiSelectEnumProperty(String propertyLabel, Module parentModule, T... propertyValues) {
        super(propertyLabel, Arrays.asList(propertyValues), parentModule);
        this.constantEnumValues = (T[]) getPropertyValue().get(0).getClass().getEnumConstants();
    }

    public void selectEnumValue(T value) {
        if (!getPropertyValue().contains(value))
            getPropertyValue().add(value);
    }

    public void setValue(int index) {
        final List<T> values = new ArrayList<>(getPropertyValue());
        final T referencedVariant = this.getConstantEnumValues()[index];
        if (values.contains(referencedVariant)) {
            values.remove(referencedVariant);
        } else {
            values.add(referencedVariant);
        }
        setPropertyValue(values);
    }

    public List<T> getSelectedValues() {
        return getPropertyValue();
    }

    public boolean isSelected(T value) {
        return getPropertyValue().contains(value);
    }

    public T[] getConstantEnumValues() {
        return constantEnumValues;
    }
}
