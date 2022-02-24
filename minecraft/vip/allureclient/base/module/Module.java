package vip.allureclient.base.module;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.poskwatch.eventbus.api.annotations.EventHandler;
import io.github.poskwatch.eventbus.api.enums.Priority;
import io.github.poskwatch.eventbus.api.interfaces.IEventCallable;
import io.github.poskwatch.eventbus.api.interfaces.IEventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import org.luaj.vm2.ast.Str;
import vip.allureclient.AllureClient;
import vip.allureclient.base.bind.BindableObject;
import vip.allureclient.base.config.ConfigurableObject;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.module.interfaces.ToggleableObject;
import vip.allureclient.base.property.Property;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.base.util.visual.AnimatedCoordinate;
import vip.allureclient.impl.event.events.network.PacketReceiveEvent;
import vip.allureclient.impl.property.*;
import vip.allureclient.visual.notification.NotificationType;

import java.awt.*;
import java.util.Objects;

public class Module implements ToggleableObject, BindableObject, ConfigurableObject {

    // Module Identifiers. (Name, category, display suffix)
    private final String moduleName;
    private final ModuleCategory moduleCategory;
    private String moduleSuffix;

    // Module data. (Bind, toggled, hidden)
    private int moduleKeyBind;
    private boolean isModuleToggled;
    private boolean hidden;

    // Minecraft class object to be able to access inner objects easily
    public final Minecraft mc = Minecraft.getMinecraft();

    // Event listener to be registered on toggle
    private IEventListener listener = new IEventListener() {};

    // Coordinate for display animation. TODO: New animation implementation
    private final AnimatedCoordinate animatedCoordinate;

    // Constructor with bind parameter
    public Module(String moduleName, int moduleKeyBind, ModuleCategory category){
        this.moduleName = moduleName;
        this.moduleKeyBind = moduleKeyBind;
        this.moduleCategory = category;
        AllureClient.getInstance().getBindManager().registerBind(moduleKeyBind, this);
        this.animatedCoordinate = new AnimatedCoordinate(GuiScreen.width, 0);
        // Update settings value changes
        AllureClient.getInstance().getPropertyManager().getPropertiesByModule(this).forEach(property -> property.onValueChange.run());
    }

    // Unbound constructor
    public Module(String moduleName, ModuleCategory category) {
        this.moduleName = moduleName;
        this.moduleCategory = category;
        this.animatedCoordinate = new AnimatedCoordinate(GuiScreen.width, 0);
        // Update settings value changes
        AllureClient.getInstance().getPropertyManager().getPropertiesByModule(this).forEach(property -> property.onValueChange.run());
    }

    // Method to set listener
    public void setListener(IEventListener listener) {
        this.listener = listener;
    }

    // Getter for module name
    public String getModuleName(){
        return moduleName;
    }

    // Getter for module category
    public ModuleCategory getModuleCategory() {
        return moduleCategory;
    }

    // Setter for module suffix, called within module implementation
    public void setModuleSuffix(String moduleSuffix) {
        this.moduleSuffix = moduleSuffix;
    }

    // Getter for module display name, used for displayed list
    public String getModuleDisplayName() {
        return this.moduleSuffix == null ? getModuleName() :
                getModuleName() + " \2477" + this.moduleSuffix;
    }

    // Getter for if module is toggled, implementation of ToggleableObject
    @Override
    public boolean isToggled() {
        return isModuleToggled;
    }

    // Setter for if module is toggled, implementation of ToggleableObject
    @Override
    public void setToggled(boolean toggled) {
        isModuleToggled = toggled;
        if (isModuleToggled)
            onEnable();
        else
            onDisable();
    }

    // Toggle method, implementation of ToggleableObject
    @Override
    public void toggle() {
        isModuleToggled = !isModuleToggled;
        if (isModuleToggled)
            onEnable();
        else
            onDisable();
        AllureClient.getInstance().getNotificationManager().addNotification("Module toggled",
                String.format("Module %s was %s", moduleName, isModuleToggled ? "enabled" : "disabled"), 1000, NotificationType.INFO);
    }

    // On enable method, implementation of ToggleableObject
    @Override
    public void onEnable() {
        // Register listener
        Wrapper.getEventBus().registerListener(this.listener);
    }

    // On disable method, implementation of ToggleableObject
    @Override
    public void onDisable() {
        // Unregister listener
        Wrapper.getEventBus().unregisterListener(this.listener);
    }

    // Method to register a flag listener, so modules toggle on flag
    // Meant to be called in modules that should disable on flag
    public void registerFlagListener() {
        Wrapper.getEventBus().registerListener(new IEventListener() {
            @EventHandler(events = PacketReceiveEvent.class, priority = Priority.VERY_HIGH)
            final IEventCallable<PacketReceiveEvent> onReceivePacket = (event -> {
                final Packet<?> receivedPacket = event.getPacket();
                if (receivedPacket instanceof S08PacketPlayerPosLook && isToggled()) {
                    setToggled(false);
                    // Post notifier
                    AllureClient.getInstance().getNotificationManager().addNotification(
                            String.format("%s Auto Disabled", getModuleName()),
                            String.format("%s was automatically disabled to prevent flags", getModuleName()),
                            2500,
                            NotificationType.WARNING
                    );
                }
            });
        });
    }


    // Getter for module bind, implementation of BindableObject
    @Override
    public int getBind() {
        return moduleKeyBind;
    }

    // On pressed method, implementation of BindableObject
    @Override
    public void onPressed() {
        toggle();
    }

    // Unbind method, implementation of BindableObject
    @Override
    public void unbind() {
        AllureClient.getInstance().getBindManager().unbind(this);
        this.moduleKeyBind = 0;
    }

    // Setter for module bind, implementation of BindableObject    @Override
    public void setBind(int bind) {
        AllureClient.getInstance().getBindManager().unbind(this);
        AllureClient.getInstance().getBindManager().registerBind(bind, this);
        this.moduleKeyBind = bind;
    }

    // Getter for animated coordinate
    public AnimatedCoordinate getAnimatedCoordinate() {
        return animatedCoordinate;
    }

    // Getter for if the module is hidden
    public boolean isHidden() {
        return hidden;
    }

    // Setter for if the module is hidden
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    // Getter for if the module should be drawn/displayed
    public boolean isVisible() {
        return !isHidden() && isToggled();
    }

    @SuppressWarnings("unchecked")
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
                            if (enumProperty.getPropertyEnumConstants()[i].toString().equals(enumValue)) {
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
