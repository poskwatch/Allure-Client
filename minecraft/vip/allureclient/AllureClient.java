package vip.allureclient;

import vip.allureclient.base.bind.BindManager;
import vip.allureclient.base.bind.Bindable;
import vip.allureclient.base.event.Event;
import vip.allureclient.base.event.EventManager;
import vip.allureclient.base.font.FontManager;
import vip.allureclient.base.module.ModuleManager;
import vip.allureclient.base.property.PropertyManager;
import vip.allureclient.visual.screens.dropdown.GuiDropDown;

import java.util.function.Consumer;

public class AllureClient {

    public final String CLIENT_NAME = "Allure";
    public final double CLIENT_VERSION = 0.1;
    private static final AllureClient INSTANCE = new AllureClient();

    private ModuleManager moduleManager;
    private EventManager<Event> eventManager;
    private FontManager fontManager;
    private PropertyManager propertyManager;
    private BindManager<Bindable> bindManager;

    public Consumer<AllureClient> onClientStart = (allureClient -> {
        System.out.printf("Starting %s Client. Version %s%n", allureClient.CLIENT_NAME, allureClient.CLIENT_VERSION);
        eventManager = new EventManager<>();
        fontManager = new FontManager();
        propertyManager = new PropertyManager();
        bindManager = new BindManager<>();
        moduleManager = new ModuleManager();

        GuiDropDown.onStartTask.run();
    });

    public Consumer<AllureClient> onClientExit = (allureClient -> {
        System.out.printf("Exiting %s Client. Version %s%n", allureClient.CLIENT_NAME, allureClient.CLIENT_VERSION);
        System.out.println("Exited Safely!");
    });

    public ModuleManager getModuleManager(){
        return moduleManager;
    }

    public EventManager<Event> getEventManager() {
        return eventManager;
    }

    public FontManager getFontManager() {
        return fontManager;
    }

    public PropertyManager getPropertyManager() {
        return propertyManager;
    }

    public BindManager<Bindable> getBindManager() {
        return bindManager;
    }

    public static AllureClient getInstance(){
        return INSTANCE;
    }
}
