package vip.allureclient.base.util.player;

import net.minecraft.client.Minecraft;

public class PlayerUtil {

    public static String getGameMode() {
        if (Minecraft.getMinecraft().thePlayer.capabilities.isCreativeMode)
            return "Creative";
        else if (Minecraft.getMinecraft().thePlayer.isSpectator())
            return "Spectator";
        else
            return "Survival";
    }

}
