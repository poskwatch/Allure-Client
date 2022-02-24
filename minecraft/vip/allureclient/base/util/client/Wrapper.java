package vip.allureclient.base.util.client;

import io.github.poskwatch.eventbus.impl.bus.EventBus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.network.Packet;

public class Wrapper {

    // Wrapper class for Event Bus, TODO: Authentication management

    private static EventBus eventBus;

    public static void initEventManager() {
        eventBus = new EventBus();
    }

    public static EventBus getEventBus() {
        return eventBus;
    }

}
