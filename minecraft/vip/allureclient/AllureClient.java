package vip.allureclient;

import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import vip.allureclient.base.bind.BindManager;
import vip.allureclient.base.bind.BindableObject;
import vip.allureclient.base.command.CommandManager;
import vip.allureclient.base.config.ConfigManager;
import vip.allureclient.base.file.FileManager;
import vip.allureclient.base.font.FontManager;
import vip.allureclient.base.module.ModuleManager;
import vip.allureclient.base.property.PropertyManager;
import vip.allureclient.base.script.ScriptManager;
import vip.allureclient.base.util.client.FlagCache;
import vip.allureclient.base.util.client.Version;
import vip.allureclient.visual.notification.NotificationManager;
import vip.allureclient.visual.screens.dropdown.GuiDropDown;
import vip.allureclient.visual.screens.windowed.impl.screen.GuiWindowedUI;

public class AllureClient {

    // Client identifiers. Name, version, build.
    public final String CLIENT_NAME = "Allure";

    // Version Object
    private final Version version = new Version(0, 1,
            new char[]{'0', '2', '1', '0', '2', '0', '2', '2'});

    // Client instance, used to get managers and identifiers.
    private static final AllureClient INSTANCE = new AllureClient();

    // Client managers for modules, properties, configs, etc.
    private ModuleManager moduleManager;
    private FontManager fontManager;
    private PropertyManager propertyManager;
    private BindManager<BindableObject> bindManager;
    private FileManager fileManager;
    private ConfigManager configManager;
    private NotificationManager notificationManager;
    private ScriptManager scriptManager;

    // Client UI instance for easy access
    private final GuiDropDown guiDropDown = new GuiDropDown();

    private final GuiWindowedUI guiWindowedUI = new GuiWindowedUI();

    // Method called on Minecraft's post initiation phase
    public void onPostInitiation() {
        // Print starting log
        System.out.printf("Starting %s Client. Version %s, Build %s\n", CLIENT_NAME, this.getVersion().toString(), this.getVersion().getBuild());
        // Add runtime shutdown hook to call on exit
        Runtime.getRuntime().addShutdownHook(new Thread(this::onRuntimeClosed));
        // Instantiate managers
        this.fontManager = new FontManager();
        this.notificationManager = new NotificationManager();
        this.propertyManager = new PropertyManager();
        this.bindManager = new BindManager<>();
        this.moduleManager = new ModuleManager();
        this.fileManager = new FileManager();
        this.configManager = new ConfigManager();
        this.scriptManager = new ScriptManager();
        // Initiate client commands TODO: not shitty like this
        new CommandManager();
        CommandManager.registerCommands();
        // Run the Client UI initiation
        this.guiDropDown.onStartTask.run();
        // Register bind to open the UI with Bind Manager
        getBindManager().registerBind(Keyboard.KEY_RSHIFT,
                () -> Minecraft.getMinecraft().displayGuiScreen(guiDropDown));

        getBindManager().registerBind(Keyboard.KEY_INSERT,
                () -> Minecraft.getMinecraft().displayGuiScreen(guiWindowedUI));

        // Initiate flag cache
        FlagCache.startCache();
    }

    // Method called when runtime is exited
    public void onRuntimeClosed() {
        // Print exiting log
        System.out.printf("Exiting %s Client. Version %s, Build %s\n", CLIENT_NAME, this.getVersion().toString(), this.getVersion().getBuild());
        // Save default settings
        this.configManager.saveDefaultConfig();
    }

    // Getter for client instance
    public static AllureClient getInstance(){
        return INSTANCE;
    }

    // Needed manager getters
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

    public ScriptManager getScriptManager() {
        return scriptManager;
    }

    public Version getVersion() {
        return version;
    }
}
