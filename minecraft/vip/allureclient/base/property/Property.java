package vip.allureclient.base.property;

import vip.allureclient.AllureClient;
import vip.allureclient.base.config.ConfigurableObject;
import vip.allureclient.base.module.Module;

public class Property<T> {

    private final String propertyLabel;
    private T propertyValue;
    private boolean propertyHidden;
    private final Module parentModule;
    private boolean[] hideDependencies = {};

    public Property(String propertyLabel, T propertyValue, Module parentModule, boolean... hideDependencies){
        this.propertyLabel = propertyLabel;
        this.propertyValue = propertyValue;
        this.parentModule = parentModule;
        this.hideDependencies = hideDependencies;
        AllureClient.getInstance().getPropertyManager().getProperties().add(this);
    }

    public T getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(T propertyValue) {
        this.propertyValue = propertyValue;
    }

    public Module getParentModule() {
        return parentModule;
    }

    public String getPropertyLabel() {
        return propertyLabel;
    }

    public boolean isPropertyHidden() {
        for (boolean dependency : hideDependencies) {
            if (!dependency)
                return true;
        }
        return false;
    }

    public void setPropertyHidden(boolean propertyHidden) {
        this.propertyHidden = propertyHidden;
    }

    public Class<?> getPropertyType() {
        return propertyValue.getClass();
    }

    public Runnable onValueChange = () -> { };
}
