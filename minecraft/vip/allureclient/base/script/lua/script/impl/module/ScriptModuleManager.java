package vip.allureclient.base.script.lua.script.impl.module;

import org.luaj.vm2.LuaValue;

import java.util.ArrayList;

public class ScriptModuleManager {

    private final ArrayList<ScriptModule> scriptModules = new ArrayList<>();

    public void register(String scriptModuleName, LuaValue luaValue) {
        this.scriptModules.add(new ScriptModule(scriptModuleName, luaValue));
    }

    public ArrayList<ScriptModule> getScriptModules() {
        return scriptModules;
    }
}
