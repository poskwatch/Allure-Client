package vip.allureclient.base.script.lua.functions.impl.client;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;
import vip.allureclient.AllureClient;
import vip.allureclient.base.script.lua.functions.LuaFunction;
import vip.allureclient.base.script.lua.functions.LuaFunctionCollection;

import java.util.ArrayList;

public class ClientLuaFunctions extends LuaFunctionCollection {

    private final ArrayList<LuaFunction> luaFunctions = new ArrayList<>();

    public ClientLuaFunctions() {
        registerLuaFunction("client_name", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(AllureClient.getInstance().CLIENT_NAME);
            }
        });
    }

    @Override
    public ArrayList<LuaFunction> getLuaFunctions() {
        return luaFunctions;
    }
}
