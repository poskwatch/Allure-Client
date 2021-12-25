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
        this.constantEnumValues = getPropertyValue().get(0).getDeclaringClass().getEnumConstants();
    }

    public void setValue(int index, MultiOption multiOption) {
        final List<T> values = new ArrayList<>(getPropertyValue());
        final T referencedVariant = this.getConstantEnumValues()[index];
        if (multiOption.equals(MultiOption.TOGGLE)) {
            if (!values.contains(referencedVariant))
                values.add(referencedVariant);
            else
                values.remove(referencedVariant);
        }
        else if (multiOption.equals(MultiOption.SELECT)) {
            if (!values.contains(referencedVariant))
                values.add(referencedVariant);
        }
        else if (multiOption.equals(MultiOption.UNSELECT)) {
            values.remove(referencedVariant);
        }
        setPropertyValue(values);
    }

    public List<T> getSelectedValues() {
        return getPropertyValue();
    }

    public boolean isSelected(T value) {
        return getPropertyValue().contains(value);
    }

    public boolean isSelected(int index) {
        return getPropertyValue().contains(getConstantEnumValues()[index]);
    }

    public T[] getConstantEnumValues() {
        return constantEnumValues;
    }

    public enum MultiOption {
        SELECT,
        UNSELECT,
        TOGGLE
    }
}
