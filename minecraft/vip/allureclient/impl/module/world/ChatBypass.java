package vip.allureclient.impl.module.world;

import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.ModuleCategory;
import vip.allureclient.base.module.ModuleData;
import vip.allureclient.impl.event.player.ChatMessageSendEvent;

@ModuleData(moduleName = "Chat Bypass", keyBind = 0, category = ModuleCategory.WORLD)
public class ChatBypass extends Module {

    @EventListener
    EventConsumer<ChatMessageSendEvent> onChatMessageSendEvent;

    public ChatBypass() {
        onChatMessageSendEvent = (chatMessageSendEvent -> {
            StringBuilder stringBuilder = new StringBuilder();
            for (char character : chatMessageSendEvent.getMessage().toCharArray()) {
                stringBuilder.append(character).append(".");
            }
            chatMessageSendEvent.setMessage(stringBuilder.toString());
        });
    }

}
