package vip.allureclient.base.util.client;

import org.luaj.vm2.LuaValue;

public class LuaUtil {

    public static void tryCall(LuaValue luaValue) {
        if (!luaValue.isnil())
            luaValue.call();
    }

}
