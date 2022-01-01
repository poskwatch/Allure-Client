package vip.allureclient.base.module;

import vip.allureclient.AllureClient;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.impl.module.combat.AntiBot;
import vip.allureclient.impl.module.combat.Velocity;
import vip.allureclient.impl.module.combat.killaura.KillAura;
import vip.allureclient.impl.module.movement.*;
import vip.allureclient.impl.module.player.*;
import vip.allureclient.impl.module.visual.*;
import vip.allureclient.impl.module.world.*;

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
                    new AntiVoid(),
                    new DamageSelf(),
                    // Movement Modules
                    new Sprint(),
                    new Flight(),
                    new LongJump(),
                    new Speed(),
                    new Step(),
                    new VerusFlight(),
                    // Visual Modules
                    new HUD(),
                    new Animations(),
                    new Crosshair(),
                    new TargetHUD(),
                    new PlayerESP(),
                    new HitMarkers(),
                    new Statistics(),
                    // World Modules
                    new Atmosphere(),
                    new ChestStealer(),
                    new GameSpeed(),
                    new Scaffold(),
                    new ChatBypass(),
                    new AntiDeath(),
                    new EntityDesync(),
                    new AutoHypixel()
                }
            );
            modules.forEach(module -> AllureClient.getInstance().getPropertyManager().getPropertiesByModule(module).forEach(property -> property.onValueChange.run()));
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

    public Function<Boolean, ArrayList<Module>> getSortedDisplayModules = (isVanillaFont -> {
        ArrayList<Module> sortedDisplayModules = new ArrayList<>(getModules.get());
        sortedDisplayModules.sort(Comparator.comparingDouble(module -> (isVanillaFont ?
                Wrapper.getMinecraftFontRenderer().getStringWidth(((Module)module).getModuleDisplayName()) :
                AllureClient.getInstance().getFontManager().mediumFontRenderer.getStringWidth(((Module) module).getModuleDisplayName()))).reversed());
        return sortedDisplayModules;
    });

    public Function<String, Module> getModuleByName =
            (label -> getModules.get().stream().filter(module -> module.getModuleName().equals(label)).findFirst().orElse(null));

    public Function<Class<? extends Module>, Module> getModuleByClass =
            (moduleClass -> getModules.get().stream().filter(module -> module.getClass() == moduleClass).findFirst().orElse(null));

}
