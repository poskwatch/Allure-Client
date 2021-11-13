package vip.allureclient.base.util.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;

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
}