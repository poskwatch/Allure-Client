package vip.allureclient.impl.module.world;

import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S45PacketTitle;
import vip.allureclient.AllureClient;
import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.module.annotations.ModuleData;
import vip.allureclient.base.util.client.NetworkUtil;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.impl.event.network.PacketReceiveEvent;
import vip.allureclient.impl.module.visual.Statistics;
import vip.allureclient.impl.property.EnumProperty;
import vip.allureclient.visual.notification.NotificationType;

import java.util.Random;

@ModuleData(moduleName = "Auto Hypixel", moduleBind = 0, moduleCategory = ModuleCategory.WORLD)
public class AutoHypixel extends Module {

    @EventListener
    EventConsumer<PacketReceiveEvent> onPacketReceiveEvent;

    private final EnumProperty<KillInsultsMode> killInsultsModeProperty = new EnumProperty<>("Kill Insults", KillInsultsMode.Normal, this);

    private final String[] normalInsults = {
            "%s, get owned by allure client.",
            "you should quit the game %s",
            "why is %s so bad at the game?",
            "i just gave %s a free lobotomy",
            "%s needs allure client...",
            "%s is skeetless nn 2k20 newfag",
            "%s couldn't get good in time :/"
    };

    private final String[] polishInsults = {
            "%s jest głupia i opóźniona",
            "stań się lepszym %s",
            "%s była własnością allure client",
            "Staję się lepszy %s"
    };

    public AutoHypixel() {
        onPacketReceiveEvent = (event -> {
           if (event.getPacket() instanceof S02PacketChat) {
               if (((S02PacketChat) event.getPacket()).getChatComponent() != null) {
                   final String formattedText = ((S02PacketChat) event.getPacket()).getChatComponent().getUnformattedText();
                   if (formattedText.contains("was killed by")) {
                       final String[] wordsInMessage = formattedText.split(" ");
                       if (wordsInMessage[4].contains(Wrapper.getPlayer().getName())) {
                           Wrapper.sendPacketDirect(new C01PacketChatMessage("/achat " +
                                   (killInsultsModeProperty.getPropertyValue().equals(
                                           KillInsultsMode.Polish) ? polishInsults : normalInsults
                                   )[new Random().nextInt(((killInsultsModeProperty.getPropertyValue().equals(
                                           KillInsultsMode.Polish) ? polishInsults : normalInsults
                                   ).length))].replaceAll("%s", NetworkUtil.removeRankColorCodes(wordsInMessage[0]))));
                           Statistics.getInstance().addKill();
                       }
                   }
                   if (formattedText.toLowerCase().contains("you died")) {
                       Wrapper.getPlayer().sendChatMessage("/play solo_insane");
                       AllureClient.getInstance().getNotificationManager().addNotification("Auto Hypixel",
                               "You have been sent to a new game", 3000, NotificationType.SUCCESS);
                   }
               }
           }
           if (event.getPacket() instanceof S45PacketTitle) {
               if (event.getPacket() != null) {
                   if (((S45PacketTitle) event.getPacket()).getMessage() != null && ((S45PacketTitle) event.getPacket()).getMessage().getFormattedText() != null) {
                       final String message = ((S45PacketTitle) event.getPacket()).getMessage().getFormattedText();
                       if (message.toLowerCase().contains("you died") || message.toLowerCase().contains("victory")) {
                           Wrapper.getPlayer().sendChatMessage("/play solo_insane");
                           AllureClient.getInstance().getNotificationManager().addNotification("Auto Hypixel",
                                   "You have been sent to a new game", 3500, NotificationType.SUCCESS);
                           if (message.toLowerCase().contains("victory"))
                               Statistics.getInstance().addWin();
                       }
                   }
               }
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

    private enum KillInsultsMode {
        Normal,
        Polish
    }
}
