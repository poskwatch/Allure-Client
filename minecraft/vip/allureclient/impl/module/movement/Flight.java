package vip.allureclient.impl.module.movement;

import org.lwjgl.input.Keyboard;
import vip.allureclient.AllureClient;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.event.Listener;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.ModuleCategory;
import vip.allureclient.base.module.ModuleData;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.base.util.visual.ChatUtil;
import vip.allureclient.impl.event.player.UpdatePositionEvent;
import vip.allureclient.impl.property.EnumProperty;
import vip.allureclient.impl.property.ValueProperty;

@ModuleData(label = "Flight", keyBind = Keyboard.KEY_G, category = ModuleCategory.MOVEMENT)
public class Flight extends Module {

    public Flight() {
        onUpdatePositionEvent = (updatePositionEvent -> {
           switch (flightMode.getPropertyValue()){
               case Vanilla:
                   Wrapper.getPlayer().motionY = 0;
                   break;
               case Watchdog:
                   Wrapper.getPlayer().motionY = 0.1;
                   break;
           }
        });

        onModuleEnabled = () -> {
            ChatUtil.sendMessageToPlayer("I just enabled flight! Mode: " + flightMode.getEnumValueAsString());
            ChatUtil.sendMessageToPlayer("Settings: " + AllureClient.getInstance().getPropertyManager().getOptions(this).size());
        };

        onModuleDisabled = () -> {
            ChatUtil.sendMessageToPlayer("I just disabled flight!");
        };
    }

    @EventListener
    Listener<UpdatePositionEvent> onUpdatePositionEvent;

    EnumProperty<flightModes> flightMode = new EnumProperty<>("Flight Mode", flightModes.Vanilla, this);

    ValueProperty<Double> flightSpeed = new ValueProperty<Double>("Flight Speed", 0D, 0D, 10D, this);

    enum flightModes {
        Vanilla,
        Watchdog
    }

}
