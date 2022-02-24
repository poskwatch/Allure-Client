package vip.allureclient.base.script.lua.script.impl.module;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import vip.allureclient.base.module.interfaces.ToggleableObject;

public class ScriptModule implements ToggleableObject {

    private boolean toggled;
    private final String name;
    private final LuaValue luaValue;

    public ScriptModule(String name, LuaValue luaValue) {
        this.name = name;
        this.luaValue = luaValue;
    }

    @Override
    public boolean isToggled() {
        return toggled;
    }

    @Override
    public void setToggled(boolean toggled) {
        this.toggled = toggled;
    }

    @Override
    public void toggle() {

    }

    @Override
    public void onEnable() {
        try {
            luaValue.invokemethod("on_enable");
        }
        catch (LuaError e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        try {
            luaValue.invokemethod("on_disable");
        }
        catch (LuaError e) {
            e.printStackTrace();
        }
    }

}
