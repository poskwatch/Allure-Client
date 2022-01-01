package vip.allureclient;

import org.lwjgl.input.Keyboard;
import vip.allureclient.base.bind.BindManager;
import vip.allureclient.base.bind.BindableObject;
import vip.allureclient.base.command.CommandManager;
import vip.allureclient.base.config.ConfigManager;
import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.file.FileManager;
import vip.allureclient.base.font.FontManager;
import vip.allureclient.base.module.ModuleManager;
import vip.allureclient.base.property.PropertyManager;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.impl.event.client.ClientExitEvent;
import vip.allureclient.impl.event.client.ClientStartEvent;
import vip.allureclient.visual.notification.NotificationManager;
import vip.allureclient.visual.screens.dropdown.GuiDropDown;

public class AllureClient {

    public final String CLIENT_NAME = "Allure";
    public String CUSTOM_CLIENT_NAME = CLIENT_NAME;
    public final double CLIENT_VERSION = 0.1;
    private static final AllureClient INSTANCE = new AllureClient();

    private ModuleManager moduleManager;
    private FontManager fontManager;
    private PropertyManager propertyManager;
    private BindManager<BindableObject> bindManager;
    private FileManager fileManager;
    private ConfigManager configManager;
    private CommandManager commandManager;
    private NotificationManager notificationManager;
    private final GuiDropDown guiDropDown = new GuiDropDown();

    @EventListener
    @SuppressWarnings("unused")
    EventConsumer<ClientStartEvent> onClientStart = (event -> {
        System.out.printf("Starting %s Client. Version %s%n", getInstance().CLIENT_NAME, getInstance().CLIENT_VERSION);
        this.fontManager = new FontManager();
        this.notificationManager = new NotificationManager();
        this.propertyManager = new PropertyManager();
        this.bindManager = new BindManager<>();
        this.moduleManager = new ModuleManager();
        this.fileManager = new FileManager();
        this.configManager = new ConfigManager();
        this.commandManager = new CommandManager();
        this.guiDropDown.onStartTask.run();
        getBindManager().registerBind(Keyboard.KEY_RSHIFT, () -> Wrapper.getMinecraft().displayGuiScreen(guiDropDown));
    });

    @EventListener
    @SuppressWarnings("unused")
    EventConsumer<ClientExitEvent> onClientExit = (event -> {
        System.out.printf("Exiting %s Client. Version %s%n", getInstance().CLIENT_NAME, getInstance().CLIENT_VERSION);
        System.out.println("Exited Safely!");
    });

    public ModuleManager getModuleManager(){
        return moduleManager;
    }

    public FontManager getFontManager() {
        return fontManager;
    }

    public PropertyManager getPropertyManager() {
        return propertyManager;
    }

    public BindManager<BindableObject> getBindManager() {
        return bindManager;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public NotificationManager getNotificationManager() {
        return notificationManager;
    }

    public static AllureClient getInstance(){
        return INSTANCE;
    }
}
