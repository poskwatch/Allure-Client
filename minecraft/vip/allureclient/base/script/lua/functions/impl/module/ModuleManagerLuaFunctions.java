package vip.allureclient.base.script.lua.functions.impl.module;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;
import vip.allureclient.AllureClient;
import vip.allureclient.base.script.lua.functions.LuaFunction;
import vip.allureclient.base.script.lua.functions.LuaFunctionCollection;

import java.util.ArrayList;

public class ModuleManagerLuaFunctions extends LuaFunctionCollection {

    private final ArrayList<LuaFunction> luaFunctions = new ArrayList<>();

    public ModuleManagerLuaFunctions() {
        this.registerLuaFunction("register_module", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue luaValue, LuaValue luaValue1) {
                AllureClient.getInstance().getScriptManager().
                        getScriptModuleManager().register(luaValue.checkjstring(), luaValue1);
                return null;
            }
        });
    }

    @Override
    public ArrayList<LuaFunction> getLuaFunctions() {
        return luaFunctions;
    }

}
