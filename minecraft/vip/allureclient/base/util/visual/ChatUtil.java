package vip.allureclient.base.util.visual;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import vip.allureclient.base.util.client.Wrapper;

public class ChatUtil {

    public static void sendMessageToPlayer(String message){
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(message));
    }

}
