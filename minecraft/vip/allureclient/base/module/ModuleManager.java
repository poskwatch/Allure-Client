package vip.allureclient.base.module;

import vip.allureclient.impl.module.combat.KillAura;
import vip.allureclient.impl.module.movement.Flight;
import vip.allureclient.impl.module.movement.Sprint;
import vip.allureclient.impl.module.visual.HUD;

import java.util.ArrayList;
import java.util.Arrays;

public class ModuleManager {

    private final ArrayList<Module> modules = new ArrayList<>();

    public ModuleManager(){
        final Runnable onModuleManagerStart = () -> {
            registerModules(
                    new Sprint(),
                    new KillAura(),
                    new HUD(),
                    new Flight()
            );
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

    public void onKeyPressed(int key){
        modules.forEach(module -> module.onKeyPressed(key));
    }
}
