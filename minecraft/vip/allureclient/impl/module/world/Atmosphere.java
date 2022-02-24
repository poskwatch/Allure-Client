package vip.allureclient.impl.module.world;

import io.github.poskwatch.eventbus.api.annotations.EventHandler;
import io.github.poskwatch.eventbus.api.interfaces.IEventCallable;
import io.github.poskwatch.eventbus.api.interfaces.IEventListener;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.impl.event.events.network.PacketReceiveEvent;
import vip.allureclient.impl.event.events.world.UpdateWorldTimeEvent;
import vip.allureclient.impl.property.ValueProperty;

public class Atmosphere extends Module {

    private final ValueProperty<Long> timeProperty = new ValueProperty<>("Time", 0L, 0L, 20000L, this);

    public Atmosphere(){
        super("Atmosphere", ModuleCategory.WORLD);
        this.setListener(new IEventListener() {
            @EventHandler(events = PacketReceiveEvent.class)
            final IEventCallable<PacketReceiveEvent> onReceivePacket = (event -> {
                if (event.getPacket() instanceof S03PacketTimeUpdate)
                    event.setCancelled();
            });
            @EventHandler(events = UpdateWorldTimeEvent.class)
            final IEventCallable<UpdateWorldTimeEvent> onUpdateWorldTime = (event -> {
                event.setCancelled();
                mc.theWorld.setWorldTime(timeProperty.getPropertyValue());
            });
        });
    }

    @Override
    public void onEnable() {
        mc.theWorld.setWorldTime(timeProperty.getPropertyValue());
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
