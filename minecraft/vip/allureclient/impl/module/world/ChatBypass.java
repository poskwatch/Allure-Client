package vip.allureclient.impl.module.world;

import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.module.annotations.ModuleData;
import vip.allureclient.impl.event.player.ChatMessageSendEvent;

@ModuleData(moduleName = "Chat Bypass", moduleBind = 0, moduleCategory = ModuleCategory.WORLD)
public class ChatBypass extends Module {

    @EventListener
    EventConsumer<ChatMessageSendEvent> onChatMessageSendEvent;

    public ChatBypass() {
        onChatMessageSendEvent = (chatMessageSendEvent -> {
            StringBuilder stringBuilder = new StringBuilder();
            if (!chatMessageSendEvent.getMessage().startsWith("/")) {
                for (char character : chatMessageSendEvent.getMessage().toCharArray()) {
                    stringBuilder.append(character).append(".");
                }
                chatMessageSendEvent.setMessage(stringBuilder.toString());
            }
        });
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
