package vip.allureclient.base.script.lua.functions;

import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.LibFunction;

public abstract class MultiArgFunction extends LibFunction {

    public abstract Varargs invoke(Varargs var1);

}
