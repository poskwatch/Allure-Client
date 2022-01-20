package vip.allureclient.base.script.lua.functions.impl;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.LibFunction;

public abstract class MultiArgFunction extends LibFunction {

    public abstract LuaValue call();

}
