package vip.allureclient.impl.module.player;

import io.github.poskwatch.eventbus.api.annotations.EventHandler;
import io.github.poskwatch.eventbus.api.enums.Priority;
import io.github.poskwatch.eventbus.api.interfaces.IEventCallable;
import io.github.poskwatch.eventbus.api.interfaces.IEventListener;
import net.minecraft.network.play.client.C03PacketPlayer;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.util.client.Stopwatch;
import vip.allureclient.base.util.player.MovementUtil;
import vip.allureclient.impl.event.events.network.PacketSendEvent;
import vip.allureclient.impl.event.events.player.UpdatePositionEvent;
import vip.allureclient.impl.property.ValueProperty;

public class AntiVoid extends Module {

    private final ValueProperty<Double> distanceProperty = new ValueProperty<>("Distance", 5.0D, 3.0D, 10.0D, this);

    private final Stopwatch ticksSincePullTimer = new Stopwatch();

    public AntiVoid() {
        super("Anti Void", ModuleCategory.PLAYER);
        this.setListener(new IEventListener() {
            @EventHandler(events = UpdatePositionEvent.class, priority = Priority.HIGH)
            final IEventCallable<UpdatePositionEvent> onUpdatePosition = (event -> {
                if (event.isPre()) {
                    if (mc.thePlayer.fallDistance >= distanceProperty.getPropertyValue() && ticksSincePullTimer.hasReached(500) && MovementUtil.isOverVoid()) {
                        event.setY(event.getY() + 5.0D + StrictMath.random());
                        mc.thePlayer.fallDistance = 0;
                        ticksSincePullTimer.reset();
                    }
                }
            });
            @EventHandler(events = PacketSendEvent.class)
            final IEventCallable<PacketSendEvent> onSendPacket = (event -> {
                if (MovementUtil.isOverVoid() && mc.thePlayer.fallDistance > 1 && event.getPacket() instanceof C03PacketPlayer)
                    event.setCancelled();
            });
        });
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
