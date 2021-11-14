package vip.allureclient.base.util.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.network.Packet;

public class Wrapper {

    public static Minecraft getMinecraft(){
        return Minecraft.getMinecraft();
    }

    public static EntityPlayerSP getPlayer(){
        return getMinecraft().thePlayer;
    }

    public static WorldClient getWorld(){
        return getMinecraft().theWorld;
    }

    public static void sendPacketDirect(Packet packet) {
        getMinecraft().getNetHandler().getNetworkManager().sendPacket(packet);
    }
}
