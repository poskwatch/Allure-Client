package vip.allureclient.base.script.lua.functions;

import org.luaj.vm2.LuaValue;

public class LuaFunction {

    private final String functionName;
    private final LuaValue function;

    public LuaFunction(String functionName, LuaValue function) {
        this.functionName = functionName;
        this.function = function;
    }

    public LuaValue getFunction() {
        return function;
    }

    public String getFunctionName() {
        return functionName;
    }
}

