package vip.allureclient.impl.module.visual;

import vip.allureclient.AllureClient;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.util.visual.ChatUtil;
import vip.allureclient.impl.property.EnumProperty;
import vip.allureclient.impl.property.ValueProperty;

public class Animations extends Module {

    public Animations() {
        super("Animations", ModuleCategory.VISUAL);
    }

    public final EnumProperty<BlockMode> blockModeProperty = new EnumProperty<>("Block Mode", BlockMode.SWING, this);
    public final ValueProperty<Double> slowDownProperty = new ValueProperty<>("Slowdown", 0D, 0D, 6D, this);
    public final ValueProperty<Double> xValueProperty = new ValueProperty<>("X", 0D, -0.4D, 0.4D, this);
    public final ValueProperty<Double> yValueProperty = new ValueProperty<>("Y", 0D, -0.4D, 0.4D, this);
    public final ValueProperty<Double> zValueProperty = new ValueProperty<>("Z", 0D, -0.4D, 0.4D, this);
    public final ValueProperty<Double> itemScaleProperty = new ValueProperty<>("Item Scale", 1.0D, 0.1D, 2.0D, this);

    @Override
    public void onEnable() {
        setToggled(false);
        ChatUtil.sendMessageToPlayer("This module does not toggle.");
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public enum BlockMode {
        NORMALFUCKINGGAYRETARD("1.7"),
        SWING("Swing"),
        SWONG("Swong"),
        SWANG("Swang"),
        SWANK("Swank"),
        SLIDE("Slide"),
        FLUX("Flux"),
        EXHIBITION("Exhibition");

        private final String name;

        BlockMode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static Animations getInstance() {
        return (Animations) AllureClient.getInstance().getModuleManager().getModuleOrNull("Animations");
    }
}
