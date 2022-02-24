package vip.allureclient.base.script.lua.functions.impl.player;

import net.minecraft.client.Minecraft;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;
import vip.allureclient.base.script.lua.functions.LuaFunction;
import vip.allureclient.base.script.lua.functions.LuaFunctionCollection;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.base.util.player.MovementUtil;

import java.util.ArrayList;

public class PlayerLuaFunctions extends LuaFunctionCollection {

    private final ArrayList<LuaFunction> luaFunctions = new ArrayList<>();
    private final Minecraft mc = Minecraft.getMinecraft();

    public PlayerLuaFunctions() {
        registerLuaFunction("send_chat_message", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue luaValue) {
                mc.thePlayer.sendChatMessage(luaValue.checkjstring());
                return null;
            }
        });
        registerLuaFunction("set_speed", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue luaValue) {
                mc.thePlayer.setSpeed(luaValue.tofloat());
                return null;
            }
        });
        registerLuaFunction("jump", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                mc.thePlayer.jump();
                return null;
            }
        });
        registerLuaFunction("on_ground", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(mc.thePlayer.onGround);
            }
        });
        registerLuaFunction("movement_direction", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(MovementUtil.getMovementDirection());
            }
        });
        registerLuaFunction("base_move_speed", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(MovementUtil.getBaseMoveSpeed());
            }
        });
        registerLuaFunction("is_moving", new ZeroArgFunction() {
            @Override
            public LuaValue call() {

                return LuaValue.valueOf(MovementUtil.isMoving());
            }
        });

        // Motion Getters

        registerLuaFunction("motion_x", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(mc.thePlayer.motionX);
            }
        });
        registerLuaFunction("motion_y", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(mc.thePlayer.motionY);
            }
        });
        registerLuaFunction("motion_z", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(mc.thePlayer.motionZ);
            }
        });

        // Motion Setters

        registerLuaFunction("set_motion_x", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue var1) {
                mc.thePlayer.motionX = var1.checkdouble();
                return null;
            }
        });
        registerLuaFunction("set_motion_y", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue var1) {
                mc.thePlayer.motionY = var1.checkdouble();
                return null;
            }
        });
        registerLuaFunction("set_motion_z", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue var1) {
                mc.thePlayer.motionZ = var1.checkdouble();
                return null;
            }
        });

        registerLuaFunction("swing_item", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                mc.thePlayer.swingItem();
                return null;
            }
        });

    }

    public ArrayList<LuaFunction> getLuaFunctions() {
        return luaFunctions;
    }
}
