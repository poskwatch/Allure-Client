package vip.allureclient.base.util.client;

import io.github.poskwatch.eventbus.api.annotations.EventHandler;
import io.github.poskwatch.eventbus.api.enums.Priority;
import io.github.poskwatch.eventbus.api.interfaces.IEventCallable;
import io.github.poskwatch.eventbus.api.interfaces.IEventListener;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import vip.allureclient.impl.event.events.network.PacketReceiveEvent;

import java.util.ArrayList;

public class FlagCache {

    // Create timestamp to flag map
    private static ArrayList<TimeStampedFlag> timeStampedFlags;

    public static void startCache() {
        timeStampedFlags = new ArrayList<>();
        // Every time you receive flag, register flag into map
        Wrapper.getEventBus().registerListener(new IEventListener() {
            @EventHandler(events = PacketReceiveEvent.class, priority = Priority.VERY_HIGH)
            final IEventCallable<PacketReceiveEvent> onReceiveFlag = (event -> {
                final Packet<?> receivedPacket = event.getPacket();
                if (event.getPacket() instanceof S08PacketPlayerPosLook) {
                    S08PacketPlayerPosLook receivedS08 = (S08PacketPlayerPosLook) event.getPacket();
                    TimeStampedFlag timeStampedFlag = new TimeStampedFlag(System.currentTimeMillis(), receivedS08);
                    timeStampedFlags.add(timeStampedFlag);
                }
            });
        });
    }

    public static TimeStampedFlag recentFlag() {
        return timeStampedFlags.get(0);
    }

    public static class TimeStampedFlag {

        private final S08PacketPlayerPosLook s08;
        private final long timeStamp;

        public TimeStampedFlag(long timeStamp, S08PacketPlayerPosLook s08) {
            this.timeStamp = timeStamp;
            this.s08 = s08;
        }

        public long getTimeStamp() {
            return timeStamp;
        }

        public S08PacketPlayerPosLook getS08() {
            return s08;
        }
    }
}
