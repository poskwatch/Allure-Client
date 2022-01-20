package vip.allureclient.base.script.lua.functions;

import org.luaj.vm2.LuaValue;

import java.util.ArrayList;

public abstract class LuaFunctionCollection {

    public abstract ArrayList<LuaFunction> getLuaFunctions();

    public void registerLuaFunction(String functionName, LuaValue function) {
        this.getLuaFunctions().add(new LuaFunction(functionName, function));
    }

} 
