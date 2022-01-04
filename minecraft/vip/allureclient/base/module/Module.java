package vip.allureclient.base.module;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.gui.GuiScreen;
import vip.allureclient.AllureClient;
import vip.allureclient.base.bind.BindableObject;
import vip.allureclient.base.config.ConfigurableObject;
import vip.allureclient.base.module.annotations.ModuleData;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.module.interfaces.ToggleableObject;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.base.util.visual.AnimatedCoordinate;
import vip.allureclient.impl.property.*;
import vip.allureclient.visual.notification.NotificationType;

import java.awt.*;
import java.util.Objects;

public class Module implements ToggleableObject, BindableObject, ConfigurableObject {

    private final String moduleName;
    private int moduleKeyBind;
    private final ModuleCategory moduleCategory;
    private boolean isModuleToggled;
    private String moduleSuffix;
    private boolean hidden;

    private final AnimatedCoordinate animatedCoordinate = new AnimatedCoordinate(GuiScreen.width, 0);

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
        return getModuleSuffix() == null ? getModuleName() : getModuleName() + " \2477" + getModuleSuffix().replaceAll("_", " ");
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
        AllureClient.getInstance().getNotificationManager().addNotification("Module toggled",
                String.format("Module %s was %s", moduleName, isModuleToggled ? "enabled" : "disabled"), 1000, NotificationType.INFO);
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

    public AnimatedCoordinate getAnimatedCoordinate() {
        return animatedCoordinate;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isVisible() {
        return !isHidden() && isToggled();
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public JsonObject save() {
        JsonObject moduleObject = new JsonObject();
        moduleObject.addProperty("toggled", isModuleToggled);
        moduleObject.addProperty("bind", moduleKeyBind);
        moduleObject.addProperty("hidden", hidden);
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
                JsonObject multiSelectObject = new JsonObject();
                for (int i = 0; i < multiSelectEnumProperty.getConstantEnumValues().length; i++) {
                    multiSelectObject.addProperty(multiSelectEnumProperty.getConstantEnumValues()[i].name(),
                            multiSelectEnumProperty.isSelected(i));
                }
                propertiesObject.add(multiSelectEnumProperty.getPropertyLabel(), multiSelectObject);
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
        if (jsonObject.has("hidden")) {
            setHidden(jsonObject.get("hidden").getAsBoolean());
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
                        JsonObject object = propertiesObject.getAsJsonObject(multiSelectEnumProperty.getPropertyLabel());
                        for (int i = 0; i < multiSelectEnumProperty.getConstantEnumValues().length; i++) {
                            if (object.has(multiSelectEnumProperty.getConstantEnumValues()[i].name())) {
                                if (object.get(multiSelectEnumProperty.getConstantEnumValues()[i].name()).getAsBoolean())
                                    multiSelectEnumProperty.setValue(i, MultiSelectEnumProperty.MultiOption.SELECT);
                                else
                                    multiSelectEnumProperty.setValue(i, MultiSelectEnumProperty.MultiOption.UNSELECT);
                            }
                        }
                    }
                }
            });
        }
    }
}
