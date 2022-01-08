package vip.allureclient.base.script.lua.script;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.module.interfaces.ToggleableObject;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.impl.event.player.UpdatePositionEvent;

public class ScriptModule implements ToggleableObject {

    private boolean toggled;
    private final String name;
    private final LuaValue luaValue;

    public ScriptModule(String name, LuaValue luaValue) {
        this.name = name;
        this.luaValue = luaValue;

        this.onUpdatePositionEvent = (event -> {});

        if (!luaValue.get("update_position_event").isnil()) {
            this.onUpdatePositionEvent = (event -> luaValue.get("update_position_event").call());
        }
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
        Wrapper.getEventManager().subscribe(this);
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
        Wrapper.getEventManager().unsubscribe(this);
    }

    // Events

    @EventListener
    private EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;
}
