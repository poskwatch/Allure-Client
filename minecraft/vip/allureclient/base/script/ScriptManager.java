package vip.allureclient.base.script;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;
import vip.allureclient.AllureClient;
import vip.allureclient.base.script.lua.functions.impl.client.ClientLuaFunctions;
import vip.allureclient.base.script.lua.functions.impl.module.ModuleManagerLuaFunctions;
import vip.allureclient.base.script.lua.functions.impl.player.PlayerLuaFunctions;
import vip.allureclient.base.script.lua.script.Script;
import vip.allureclient.base.script.lua.script.impl.module.ScriptModule;
import vip.allureclient.base.script.lua.script.impl.module.ScriptModuleManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Stream;

public class ScriptManager {

    private final Globals globals = JsePlatform.standardGlobals();
    private final ScriptModuleManager scriptModuleManager;

    private final File SCRIPTS_DIRECTORY = new File(AllureClient.getInstance().getFileManager().getClientDirectory() + "/scripts");
    private final ArrayList<Script> scripts = new ArrayList<>();

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

        // Init module manager
        this.scriptModuleManager = new ScriptModuleManager();

        // Setup directory

        if (!SCRIPTS_DIRECTORY.exists())
            if (SCRIPTS_DIRECTORY.mkdirs())
                System.out.println("Successfully created scripts directory");
        for (File file : Objects.requireNonNull(SCRIPTS_DIRECTORY.listFiles())) {
            if (file.getName().endsWith(".lua")) {
                StringBuilder sb = new StringBuilder();
                try (Stream<String> stream = Files.lines(Paths.get(file.getPath()))) {
                    stream.forEach(s -> sb.append(s).append("\n"));
                    scripts.add(new Script(sb.toString()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void runScript(String script) {
        LuaValue chunk = globals.load(script);
        chunk.call();
    }

    public void runTestScript() {
        runScript("player.multi_arg('th', 'th')");
    }

    public ScriptModuleManager getScriptModuleManager() {
        return scriptModuleManager;
    }

    public ArrayList<Script> getScripts() {
        return scripts;
    }
}
