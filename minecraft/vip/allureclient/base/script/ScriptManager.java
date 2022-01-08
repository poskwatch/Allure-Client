package vip.allureclient.base.script;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;
import vip.allureclient.base.script.lua.functions.impl.client.ClientLuaFunctions;
import vip.allureclient.base.script.lua.functions.impl.module.ModuleManagerLuaFunctions;
import vip.allureclient.base.script.lua.functions.impl.player.PlayerLuaFunctions;
import vip.allureclient.base.script.lua.script.ScriptModule;
import vip.allureclient.base.script.lua.script.ScriptModuleManager;

public class ScriptManager {

    private final Globals globals = JsePlatform.standardGlobals();
    private final ScriptModuleManager scriptModuleManager = new ScriptModuleManager();

    public ScriptManager() {
        // Setup script modules

        // Client Methods
        globals.set("client", new LuaTable());
        new ClientLuaFunctions().getLuaFunctions().forEach(luaFunction -> globals.get("client").set(luaFunction.getFunctionName(), luaFunction.getFunction()));

        // Module Manager
        globals.set("module_manager", new LuaTable());
        new ModuleManagerLuaFunctions().getLuaFunctions().forEach(luaFunction -> globals.get("module_manager").set(luaFunction.getFunctionName(), luaFunction.getFunction()));

        // Player Methods
        globals.set("player", new LuaTable());
        new PlayerLuaFunctions().getLuaFunctions().forEach(luaFunction -> globals.get("player").set(luaFunction.getFunctionName(), luaFunction.getFunction()));

    }

    public void runScript(String script) {
        LuaValue chunk = globals.load(script);
        chunk.call();
    }

    public void runTestScript() {
        final String script = "local test = {\n" +
                "    on_enable = function()\n" +
                "        print('sus')\n" +
                "    end,  \n" +
                "    render_2D_event = function()\n" +
                "        player.rect();\n" +
                "    end    \n" +
                "}\n" +
                "module_manager.register_module('test', test);";
        runScript(script);

        scriptModuleManager.getScriptModules().forEach(ScriptModule::onEnable);
    }

    public ScriptModuleManager getScriptModuleManager() {
        return scriptModuleManager;
    }
}
