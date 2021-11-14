package vip.allureclient.impl.module.combat;

import org.lwjgl.input.Keyboard;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.ModuleCategory;
import vip.allureclient.base.module.ModuleData;
import vip.allureclient.base.util.visual.ChatUtil;

@ModuleData(moduleName = "KillAura", keyBind = Keyboard.KEY_Y, category = ModuleCategory.COMBAT)
public class KillAura extends Module {

    public KillAura(){
        this.onModuleEnabled = () -> {

        };

        this.onModuleDisabled = () -> {

        };
    }

}
