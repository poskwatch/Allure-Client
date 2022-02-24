package vip.allureclient.base.module;

import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.impl.module.combat.*;
import vip.allureclient.impl.module.movement.*;
import vip.allureclient.impl.module.player.*;
import vip.allureclient.impl.module.visual.*;
import vip.allureclient.impl.module.visual.hud.HUD;
import vip.allureclient.impl.module.world.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ModuleManager {

    // Use a string to instance map, with the module name and module instance
    private final Map<String, Module> stringToModuleInstanceMap;

    // Method to put module(s) into map (ignoring case by using toLowerCase())
    private void registerToInstanceMap(Module... modules) {
        Arrays.asList(modules).forEach(module -> this.stringToModuleInstanceMap.put(module.getModuleName().toLowerCase(), module));
    }

    public ModuleManager(){
        // Instantiate map using HashMap implementation. Very fast for this context
        this.stringToModuleInstanceMap = new HashMap<>();
        // Put all module instances into map

        this.registerToInstanceMap(
                // Combat Modules
                new KillAura(),
                new Velocity(),
                new AntiBot(),
                new AimBot(),
                new AutoArmor(),
                // Player Modules
                new NoFall(),
                new Disabler(),
                new NoSlowdown(),
                new GuiMove(),
                new AntiVoid(),
                new Regeneration(),
                new Blink(),
                // Movement Modules
                new Sprint(),
                new Flight(),
                new LongJump(),
                new Speed(),
                new Step(),
                new PacketSpeed(),
                new WatchdogFlight(),
                new SpartanFly(),
                // Visual Modules
                new HUD(),
                new Animations(),
                new Crosshair(),
                new TargetHUD(),
                new PlayerESP(),
                new HitMarkers(),
                new Statistics(),
                new Brightness(),
                // World Modules
                new Atmosphere(),
                new ChestStealer(),
                new GameSpeed(),
                new Scaffold(),
                new EntityDesync(),
                new AutoHypixel()
        );
        // TODO: handle property change, etc
    }

    // Getter for module by its name, uses string to instance map to find (ignoring case by using toLowerCase())
    public Module getModuleOrNull(String moduleName) {
        return this.stringToModuleInstanceMap.get(moduleName.toLowerCase());
    }

    // Getter for modules in array list, used for displaying modules
    public ArrayList<Module> getModulesAsArraylist() {
        return new ArrayList<>(this.stringToModuleInstanceMap.values());
    }

    // Used to get modules by their categories, used for GUI panels
    public ArrayList<Module> getModulesByCategory(ModuleCategory moduleCategory) {
        return this.stringToModuleInstanceMap.values().stream().filter(module ->
                module.getModuleCategory().equals(moduleCategory)).collect(Collectors.toCollection(ArrayList::new));
    }
}
