package vip.allureclient.base.property;

import vip.allureclient.base.module.Module;

import java.util.ArrayList;

public class PropertyManager {

    private final ArrayList<Property<?>> properties;

    public PropertyManager() {
        properties = new ArrayList<>();
    }

    public ArrayList<Property<?>> getProperties() {
        return properties;
    }

    public ArrayList<Property<?>> getPropertiesByModule(Module module){
        ArrayList<Property<?>> filteredOptions = new ArrayList<>();
        getProperties().stream().filter(property -> property.getParentModule() == module).forEach(filteredOptions::add);
        return filteredOptions;
    }
}
