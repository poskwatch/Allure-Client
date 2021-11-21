package vip.allureclient.base.module;

import vip.allureclient.base.font.MinecraftFontRenderer;
import vip.allureclient.impl.module.combat.AntiBot;
import vip.allureclient.impl.module.combat.KillAura;
import vip.allureclient.impl.module.combat.Velocity;
import vip.allureclient.impl.module.movement.Flight;
import vip.allureclient.impl.module.movement.Speed;
import vip.allureclient.impl.module.movement.Sprint;
import vip.allureclient.impl.module.movement.Step;
import vip.allureclient.impl.module.player.*;
import vip.allureclient.impl.module.visual.*;
import vip.allureclient.impl.module.world.Atmosphere;
import vip.allureclient.impl.module.world.ChatBypass;
import vip.allureclient.impl.module.world.ChestStealer;
import vip.allureclient.impl.module.world.GameSpeed;

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
            registerModules.accept(new Module[]{
                    // Combat Modules
                    new KillAura(),
                    new Velocity(),
                    new AntiBot(),
                    // Player Modules
                    new NoFall(),
                    new PingSpoof(),
                    new Disabler(),
                    new NoSlow(),
                    new GuiMove(),
                    // Movement Modules
                    new Sprint(),
                    new Flight(),
                    new Speed(),
                    new Step(),
                    // Visual Modules
                    new HUD(),
                    new ClientColor(),
                    new Animations(),
                    new Crosshair(),
                    new TargetHUD(),
                    // World Modules
                    new Atmosphere(),
                    new ChestStealer(),
                    new GameSpeed(),
                    new ChatBypass()
                }
            );
            System.out.println("Module Manager Initiated...");
        };
        onModuleManagerStart.run();
    }

    private final Consumer<Module[]> registerModules = (modulesArray -> modules.addAll(Arrays.asList(modulesArray)));

    public Supplier<ArrayList<Module>> getModules = () -> modules;

    public final Function<ModuleCategory, ArrayList<Module>> getModulesByCategory = (moduleCategory -> {
        ArrayList<Module> filteredModules = new ArrayList<>();
        getModules.get().stream().filter(module -> module.getModuleCategory() == moduleCategory).forEach(filteredModules::add);
        return filteredModules;
    });

    public Function<MinecraftFontRenderer, ArrayList<Module>> getSortedDisplayModules = (fontRenderer -> {
        ArrayList<Module> sortedDisplayModules = new ArrayList<>(getModules.get());
        sortedDisplayModules.removeIf(module -> (!module.isModuleToggled()));
        sortedDisplayModules.sort(Comparator.comparingDouble(module -> fontRenderer.getStringWidth(((Module) module).getModuleDisplayName())).reversed());
        return sortedDisplayModules;
    });

    public Function<String, Module> getModuleByName =
            (label -> getModules.get().stream().filter(module -> module.getModuleName().equals(label)).findFirst().orElse(null));

    public Function<Class<? extends Module>, Module> getModuleByClass =
            (moduleClass -> getModules.get().stream().filter(module -> module.getClass() == moduleClass).findFirst().orElse(null));

    public Consumer<Integer> onKeyPressed = (key -> modules.forEach(module -> module.onKeyPressed(key)));
}
