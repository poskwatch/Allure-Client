package vip.allureclient.base.util.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.network.Packet;
import vip.allureclient.base.event.Event;
import vip.allureclient.base.event.EventManager;

public class Wrapper {

    private static EventManager<Event> eventManager;

    public static Minecraft getMinecraft(){
        return Minecraft.getMinecraft();
    }

    public static EntityPlayerSP getPlayer(){
        return getMinecraft().thePlayer;
    }

    public static WorldClient getWorld(){
        return getMinecraft().theWorld;
    }

    public static FontRenderer getMinecraftFontRenderer() {
        return getMinecraft().fontRendererObj;
    }

    public static boolean isSinglePlayer() {
        return getMinecraft().isIntegratedServerRunning() && getMinecraft().getIntegratedServer() != null;
    }

    public static RenderManager getRenderManager() {
        return getMinecraft().getRenderManager();
    }

    public static void initEventManager() {
        eventManager = new EventManager<>();
    }

    public static EventManager<Event> getEventManager() {
        return eventManager;
    }

    public static void sendPacketDirect(Packet<?> packet) {
        getMinecraft().getNetHandler().getNetworkManager().sendPacket(packet);
    }
}
