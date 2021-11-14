package vip.allureclient.base.module;

import com.sun.org.apache.xpath.internal.operations.Mod;
import vip.allureclient.base.font.MinecraftFontRenderer;
import vip.allureclient.impl.module.combat.KillAura;
import vip.allureclient.impl.module.movement.Flight;
import vip.allureclient.impl.module.movement.Sprint;
import vip.allureclient.impl.module.player.NoFall;
import vip.allureclient.impl.module.player.PingSpoof;
import vip.allureclient.impl.module.visual.Animations;
import vip.allureclient.impl.module.visual.HUD;
import vip.allureclient.impl.module.world.Atmosphere;

import javax.xml.bind.Marshaller;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ModuleManager {

    private final ArrayList<Module> modules = new ArrayList<>();

    public ModuleManager(){
        final Runnable onModuleManagerStart = () -> {
            registerModules(
                    // Combat Modules
                    new KillAura(),
                    // Player Modules
                    new NoFall(),
                    new PingSpoof(),
                    // Movement Modules
                    new Sprint(),
                    new Flight(),
                    // Visual Modules
                    new HUD(),
                    new Animations(),
                    // World Modules
                    new Atmosphere()
            );
            System.out.println("Module Manager Initiated...");
        };
        onModuleManagerStart.run();
    }

    private void registerModules(Module... modules){
        this.modules.addAll(Arrays.asList(modules));
    }

    public ArrayList<Module> getModules(){
        return modules;
    }

    public ArrayList<Module> getModulesByCategory(ModuleCategory category){
        ArrayList<Module> filteredModules = new ArrayList<>();
        getModules().stream().filter(module -> module.getModuleCategory() == category).forEach(filteredModules::add);
        return filteredModules;
    }

    public Function<MinecraftFontRenderer, ArrayList<Module>> getSortedDisplayModules = (fontRenderer -> {
        ArrayList<Module> sortedDisplayModules = new ArrayList<>(getModules());
        sortedDisplayModules.removeIf(module -> (!module.isModuleToggled()));
        sortedDisplayModules.sort(Comparator.comparingDouble(module -> fontRenderer.getStringWidth(((Module) module).getModuleDisplayName())).reversed());
        return sortedDisplayModules;
    });

    public Function<String, Module> getModuleByName =
            (label -> getModules().stream().filter(module -> module.getModuleName().equals(label)).findFirst().orElse(null));

    public Function<Class<? extends Module>, Module> getModuleByClass =
            (moduleClass -> getModules().stream().filter(module -> module.getClass() == moduleClass).findFirst().orElse(null));

    public Consumer<Integer> onKeyPressed = (key -> {
        modules.forEach(module -> module.onKeyPressed(key));
    });
}
