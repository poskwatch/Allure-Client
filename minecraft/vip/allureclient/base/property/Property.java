package vip.allureclient.base.property;

import vip.allureclient.AllureClient;
import vip.allureclient.base.module.Module;

public class Property<T> {

    private final String propertyLabel;
    private T propertyValue;
    private boolean propertyHidden;
    private final Module parentModule;
    private Boolean[] hideDependencies = {};

    public Property(String propertyLabel, T propertyValue, Module parentModule){
        this.propertyLabel = propertyLabel;
        this.propertyValue = propertyValue;
        this.parentModule = parentModule;
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
        return propertyHidden;
    }

    public void setPropertyHidden(boolean propertyHidden) {
        this.propertyHidden = propertyHidden;
    }

    public Runnable onValueChange = () -> { };
}
