package vip.allureclient.base.util.visual;

import net.minecraft.util.ChatComponentText;
import vip.allureclient.base.util.client.Wrapper;

public class ChatUtil {

    public static void sendMessageToPlayer(String message){
        Wrapper.getPlayer().addChatMessage(new ChatComponentText(message));
    }

}
