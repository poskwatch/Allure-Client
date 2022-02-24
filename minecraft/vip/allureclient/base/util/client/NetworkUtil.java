package vip.allureclient.base.util.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;

import javax.net.ssl.HttpsURLConnection;
import java.io.OutputStream;
import java.net.URL;

public class NetworkUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static int getPing() {
        NetworkPlayerInfo playerInfo = mc.getNetHandler().getPlayerInfo(mc.thePlayer.getUniqueID());
        return playerInfo == null ? 0 : playerInfo.getResponseTime();
    }

    public static boolean isPlayerPingValid(EntityPlayer entity) {
        NetworkPlayerInfo playerInfo = mc.getNetHandler().getPlayerInfo(entity.getUniqueID());
        return playerInfo != null && playerInfo.getResponseTime() == 1;
    }

    public static boolean isOnHypixel() {
        return getServerIP().contains("hypixel");
    }

    public static boolean isOnServer(String serverIP) {
        if (mc.getCurrentServerData() != null)
            return mc.getCurrentServerData().serverIP.equalsIgnoreCase(serverIP);
        else
            return false;
    }

    public static String getServerIP() {
        if (mc.getCurrentServerData() != null)
            return mc.getCurrentServerData().serverIP;
        else
            return "Singleplayer";
    }

    public static String removeRankColorCodes(String name) {
        return name.replaceAll("§7", "").replaceAll("§b", "").replaceAll("§a", "");
    }

}
