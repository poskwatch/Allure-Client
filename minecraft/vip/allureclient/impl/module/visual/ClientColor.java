package vip.allureclient.impl.module.visual;

import vip.allureclient.AllureClient;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.ModuleCategory;
import vip.allureclient.base.module.ModuleData;
import vip.allureclient.base.util.visual.ChatUtil;
import vip.allureclient.impl.property.ValueProperty;

import java.awt.*;

@ModuleData(moduleName = "Client Color", moduleBind = 0, moduleCategory = ModuleCategory.VISUAL)
public class ClientColor extends Module {

    private final ValueProperty<Integer> redProperty = new ValueProperty<>("Red", 255, 0, 255, this);
    private final ValueProperty<Integer> greenProperty = new ValueProperty<>("Green", 255, 0, 255, this);
    private final ValueProperty<Integer> blueProperty = new ValueProperty<>("Blue", 255, 0, 255, this);

    public Color getColor() {
        return new Color(redProperty.getPropertyValue(), greenProperty.getPropertyValue(), blueProperty.getPropertyValue());
    }

    public int getColorRGB() {
        return getColor().getRGB();
    }

    public static ClientColor getInstance() {
        return (ClientColor) AllureClient.getInstance().getModuleManager().getModuleByClass.apply(ClientColor.class);
    }

    public ClientColor() {
        onModuleEnabled = () -> {
            setModuleToggled(false);
            ChatUtil.sendMessageToPlayer("This module does not toggle.");
        };
    }

}
