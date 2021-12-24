package vip.allureclient.base.module;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import vip.allureclient.AllureClient;
import vip.allureclient.base.bind.BindableObject;
import vip.allureclient.base.config.ConfigurableObject;
import vip.allureclient.base.module.annotations.ModuleData;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.module.interfaces.ToggleableObject;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.base.util.visual.ChatUtil;
import vip.allureclient.impl.property.*;

import java.awt.*;
import java.util.Objects;

public class Module implements ToggleableObject, BindableObject, ConfigurableObject {

    private final String moduleName;
    private int moduleKeyBind;
    private final ModuleCategory moduleCategory;
    private boolean isModuleToggled;
    private String moduleSuffix;

    public Module(){
        if(isIdentified()) {
            this.moduleName = getClass().getAnnotation(ModuleData.class).moduleName();
            this.moduleKeyBind = getClass().getAnnotation(ModuleData.class).moduleBind();
            this.moduleCategory = getClass().getAnnotation(ModuleData.class).moduleCategory();
        }
        else {
            this.moduleName = "Unidentified Module";
            this.moduleKeyBind = 0;
            this.moduleCategory = ModuleCategory.COMBAT;
        }
        AllureClient.getInstance().getBindManager().registerBind(moduleKeyBind, this);
    }

    public String getModuleName(){
        return moduleName;
    }

    public String getModuleDisplayName() {
        return getModuleSuffix() == null ? getModuleName() : getModuleName() + " \2477" + getModuleSuffix();
    }

    public String getModuleSuffix() {
        return moduleSuffix;
    }

    public void setModuleSuffix(String moduleSuffix) {
        this.moduleSuffix = moduleSuffix;
    }

    public ModuleCategory getModuleCategory() {
        return moduleCategory;
    }

    private boolean isIdentified() {
        return getClass().isAnnotationPresent(ModuleData.class);
    }

    @Override
    public boolean isToggled() {
        return isModuleToggled;
    }

    @Override
    public void setToggled(boolean toggled) {
        isModuleToggled = toggled;
        if (isModuleToggled) {
            onEnable();
            Wrapper.getEventManager().subscribe(this);
        }
        else {
            Wrapper.getEventManager().unsubscribe(this);
            onDisable();
        }
    }

    @Override
    public void toggle() {
        isModuleToggled = !isModuleToggled;
        if (isModuleToggled) {
            onEnable();
            Wrapper.getEventManager().subscribe(this);
        }
        else {
            Wrapper.getEventManager().unsubscribe(this);
            onDisable();
        }
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public int getBind() {
        return moduleKeyBind;
    }

    @Override
    public void onPressed() {
        toggle();
    }

    @Override
    public void unbind() {
        AllureClient.getInstance().getBindManager().unbind(this);
        this.moduleKeyBind = 0;
    }

    @Override
    public void setBind(int bind) {
        AllureClient.getInstance().getBindManager().unbind(this);
        AllureClient.getInstance().getBindManager().registerBind(bind, this);
        this.moduleKeyBind = bind;
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public JsonObject save() {
        JsonObject moduleObject = new JsonObject();
        moduleObject.addProperty("toggled", isModuleToggled);
        moduleObject.addProperty("bind", moduleKeyBind);
        JsonObject propertiesObject = new JsonObject();
        AllureClient.getInstance().getPropertyManager().getPropertiesByModule(this).forEach(property -> {
            if (property instanceof BooleanProperty) {
                BooleanProperty booleanProperty = (BooleanProperty) property;
                propertiesObject.addProperty(booleanProperty.getPropertyLabel(), booleanProperty.getPropertyValue());
            }
            if (property instanceof ValueProperty) {
                ValueProperty<Number> valueProperty = (ValueProperty<Number>) property;
                if (valueProperty.getPropertyType().equals(Integer.class))
                    propertiesObject.addProperty(valueProperty.getPropertyLabel(), valueProperty.getPropertyValue().intValue());
                if (valueProperty.getPropertyType().equals(Float.class))
                    propertiesObject.addProperty(valueProperty.getPropertyLabel(), valueProperty.getPropertyValue().floatValue());
                if (valueProperty.getPropertyType().equals(Double.class))
                    propertiesObject.addProperty(valueProperty.getPropertyLabel(), valueProperty.getPropertyValue().doubleValue());
                if (valueProperty.getPropertyType().equals(Long.class))
                    propertiesObject.addProperty(valueProperty.getPropertyLabel(), valueProperty.getPropertyValue().longValue());
            }
            if (property instanceof EnumProperty) {
                EnumProperty<?> enumProperty = (EnumProperty<?>) property;
                propertiesObject.addProperty(enumProperty.getPropertyLabel(), enumProperty.getEnumValueAsString());
            }
            if (property instanceof MultiSelectEnumProperty) {
                MultiSelectEnumProperty<?> multiSelectEnumProperty = (MultiSelectEnumProperty<?>) property;
                JsonArray multiSelectArray = new JsonArray();
                for (Enum<?> enumObject : multiSelectEnumProperty.getSelectedValues()) {
                    multiSelectArray.add(new JsonPrimitive(enumObject.name()));
                }
                propertiesObject.add(multiSelectEnumProperty.getPropertyLabel(), multiSelectArray);
            }
            if (property instanceof ColorProperty) {
                ColorProperty colorProperty = (ColorProperty) property;
                JsonObject colorPropertyObject = new JsonObject();
                colorPropertyObject.addProperty("red", colorProperty.getPropertyValue().getRed());
                colorPropertyObject.addProperty("green", colorProperty.getPropertyValue().getGreen());
                colorPropertyObject.addProperty("blue", colorProperty.getPropertyValue().getBlue());
                colorPropertyObject.addProperty("alpha", colorProperty.getPropertyValue().getAlpha());
                propertiesObject.add(colorProperty.getPropertyLabel(), colorPropertyObject);
            }
        });
        moduleObject.add("properties", propertiesObject);
        return moduleObject;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void load(JsonObject jsonObject) {
        if (jsonObject.has("toggled") && !Objects.equals(moduleName, "HUD")) {
            setToggled(jsonObject.get("toggled").getAsBoolean());
        }
        if (jsonObject.has("bind")) {
            setBind(jsonObject.get("bind").getAsInt());
        }
        if (jsonObject.has("properties")) {
            JsonObject propertiesObject = jsonObject.getAsJsonObject("properties");
            AllureClient.getInstance().getPropertyManager().getProperties().forEach(property -> {
                if (propertiesObject.has(property.getPropertyLabel())) {
                    JsonElement propertyElement = propertiesObject.get(property.getPropertyLabel());
                    if (property instanceof ValueProperty) {
                        ValueProperty<Number> valueProperty = (ValueProperty<Number>) property;
                        if (valueProperty.getPropertyType().equals(Integer.class))
                            valueProperty.setPropertyValue(propertyElement.getAsInt());
                        if (valueProperty.getPropertyType().equals(Float.class))
                            valueProperty.setPropertyValue(propertyElement.getAsFloat());
                        if (valueProperty.getPropertyType().equals(Double.class))
                            valueProperty.setPropertyValue(propertyElement.getAsDouble());
                        if (valueProperty.getPropertyType().equals(Long.class))
                            valueProperty.setPropertyValue(propertyElement.getAsLong());
                    }
                    if (property instanceof BooleanProperty) {
                        BooleanProperty booleanProperty = (BooleanProperty) property;
                        booleanProperty.setPropertyValue(propertyElement.getAsBoolean());
                    }
                    if (property instanceof ColorProperty) {
                        ColorProperty colorProperty = (ColorProperty) property;
                        JsonObject colorObject = propertiesObject.getAsJsonObject(colorProperty.getPropertyLabel());
                        if (colorObject.has("red") &&
                        colorObject.has("green") &&
                        colorObject.has("blue") &&
                        colorObject.has("alpha")) {
                            colorProperty.setPropertyValue(
                                    new Color(
                                            colorObject.get("red").getAsInt(),
                                            colorObject.get("green").getAsInt(),
                                            colorObject.get("blue").getAsInt(),
                                            colorObject.get("alpha").getAsInt()
                                    )
                            );
                        }
                    }
                    if (property instanceof EnumProperty) {
                        EnumProperty enumProperty = (EnumProperty) property;
                        final String enumValue = propertyElement.getAsString();
                        for (int i = 0; i < enumProperty.getPropertyEnumConstants().length; i++) {
                            if (enumProperty.getPropertyEnumConstants()[i].name().equals(enumValue)) {
                                enumProperty.setPropertyValue(enumProperty.getPropertyEnumConstants()[i]);
                            }
                        }
                    }
                    if (property instanceof MultiSelectEnumProperty) {
                        MultiSelectEnumProperty multiSelectEnumProperty = (MultiSelectEnumProperty) property;
                        JsonArray array = propertiesObject.getAsJsonArray(multiSelectEnumProperty.getPropertyLabel());
                        for (JsonElement jsonElement : array) {
                            for (int i = 0; i < multiSelectEnumProperty.getConstantEnumValues().length; i++) {
                                if (jsonElement.getAsString().equalsIgnoreCase(multiSelectEnumProperty.getConstantEnumValues()[i].name())) {
                                    //multiSelectEnumProperty.selectEnumValue(multiSelectEnumProperty.getConstantEnumValues()[i]);
                                    multiSelectEnumProperty.setValue(i);
                                }
                            }
                        }
                    }
                }
            });
        }
    }
}
