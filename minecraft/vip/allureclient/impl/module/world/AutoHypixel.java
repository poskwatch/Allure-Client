package vip.allureclient.impl.module.world;

import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.server.S02PacketChat;
import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.ModuleCategory;
import vip.allureclient.base.module.ModuleData;
import vip.allureclient.base.util.client.NetworkUtil;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.impl.event.network.PacketReceiveEvent;

import java.util.Random;

@ModuleData(moduleName = "Auto Hypixel", moduleBind = 0, moduleCategory = ModuleCategory.WORLD)
public class AutoHypixel extends Module {

    @EventListener
    EventConsumer<PacketReceiveEvent> onPacketReceiveEvent;

    private static final String[] insults = {
            "%s, get owned by allure client.",
            "you should quit the game %s",
            "why is %s so bad at the game?",
            "i just gave %s a free lobotomy",
            "%s needs allure client..."
    };

    public AutoHypixel() {
        onPacketReceiveEvent = (event -> {
           if (event.getPacket() instanceof S02PacketChat) {
               final String formattedText = ((S02PacketChat) event.getPacket()).getChatComponent().getUnformattedText();
               if (formattedText.contains("was killed by")) {
                   final String[] wordsInMessage = formattedText.split(" ");
                   if (wordsInMessage[4].contains(Wrapper.getPlayer().getName()))
                       Wrapper.sendPacketDirect(new C01PacketChatMessage("/achat " +
                               insults[new Random().nextInt(insults.length)].replaceAll("%s", NetworkUtil.removeRankColorCodes(wordsInMessage[0]))));
               }
           }
        });
    }

}
