package vip.allureclient.base.util.client;

import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;

public class NetworkUtil {

    public static int getPing() {
        NetworkPlayerInfo playerInfo = Wrapper.getMinecraft().getNetHandler().getPlayerInfo(Wrapper.getPlayer().getUniqueID());
        return playerInfo == null ? 0 : playerInfo.getResponseTime();
    }

    public static boolean isPlayerPingNull(EntityPlayer entity) {
        NetworkPlayerInfo playerInfo = Wrapper.getMinecraft().getNetHandler().getPlayerInfo(entity.getUniqueID());
        return playerInfo != null && playerInfo.getResponseTime() == 1;
    }

    public static boolean isOnHypixel() {
        return getServerIP().contains("hypixel");
    }

    public static boolean isOnServer(String serverIP) {
        if (Wrapper.getMinecraft().getCurrentServerData() != null)
            return Wrapper.getMinecraft().getCurrentServerData().serverIP.equalsIgnoreCase(serverIP);
        else
            return false;
    }

    public static String getServerIP() {
        if (Wrapper.getMinecraft().getCurrentServerData() != null)
            return Wrapper.getMinecraft().getCurrentServerData().serverIP;
        else
            return "Singleplayer";
    }

    public static String removeRankColorCodes(String name) {
        return name.replaceAll("§7", "").replaceAll("§b", "").replaceAll("§a", "");
    }

}
