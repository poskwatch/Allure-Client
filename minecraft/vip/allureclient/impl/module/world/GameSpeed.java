package vip.allureclient.impl.module.world;

import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.impl.property.ValueProperty;

public class GameSpeed extends Module {

    private final ValueProperty<Float> timerSpeedProperty = new ValueProperty<>("Speed", 2.0F, 0.1F, 10.0F, this);

    public GameSpeed () {
        super("Game Speed", ModuleCategory.WORLD);
        this.timerSpeedProperty.onValueChange = () -> mc.timer.timerSpeed = timerSpeedProperty.getPropertyValue();
    }

    @Override
    public void onEnable() {
        mc.timer.timerSpeed = timerSpeedProperty.getPropertyValue();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1.0F;
        super.onDisable();
    }
}
