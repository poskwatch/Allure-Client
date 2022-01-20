package vip.allureclient.base.script.lua.bindings;

import javax.script.ScriptEngine;

public interface LuaBinding {

    void setupBindings(ScriptEngine scriptEngine);
    String getBindingPrefix();

}
