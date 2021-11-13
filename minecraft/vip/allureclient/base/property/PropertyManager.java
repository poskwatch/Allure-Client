package vip.allureclient.base.property;

import vip.allureclient.base.module.Module;

import java.util.ArrayList;

public class PropertyManager {

    private final ArrayList<Property> properties;

    public PropertyManager() {
        properties = new ArrayList<>();
    }

    public ArrayList<Property> getProperties() {
        return properties;
    }

    public ArrayList<Property> getOptions(Module module){
        ArrayList<Property> filteredOptions = new ArrayList<>();
        for (Property property : properties) {
            if(property.getParentModule() == module)
                filteredOptions.add(property);
        }
        return filteredOptions;
    }

}
