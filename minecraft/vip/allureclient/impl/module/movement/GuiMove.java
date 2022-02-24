package vip.allureclient.impl.module.movement;

import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.enums.ModuleCategory;

public class GuiMove extends Module {

    // Event-less because hooked within MovementInputFromOptions

    public GuiMove () {
        super("Gui Move", ModuleCategory.MOVEMENT);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
