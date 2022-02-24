package vip.allureclient.impl.module.world;

import io.github.poskwatch.eventbus.api.annotations.EventHandler;
import io.github.poskwatch.eventbus.api.enums.Priority;
import io.github.poskwatch.eventbus.api.interfaces.IEventCallable;
import io.github.poskwatch.eventbus.api.interfaces.IEventListener;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S45PacketTitle;
import vip.allureclient.AllureClient;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.util.client.NetworkUtil;
import vip.allureclient.base.util.client.StringUtil;
import vip.allureclient.base.util.player.PacketUtil;
import vip.allureclient.impl.event.events.network.PacketReceiveEvent;
import vip.allureclient.impl.module.visual.Statistics;
import vip.allureclient.impl.property.EnumProperty;
import vip.allureclient.visual.notification.NotificationType;

public class AutoHypixel extends Module {

    private final EnumProperty<KillInsultsMode> killInsultsModeProperty = new EnumProperty<>("Kill Insults", KillInsultsMode.NORMAL, this);

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
        super("Auto Hypixel", ModuleCategory.WORLD);
        this.setListener(new IEventListener() {
            @EventHandler(events = PacketReceiveEvent.class, priority = Priority.VERY_HIGH)
            final IEventCallable<PacketReceiveEvent> onReceivePacket = (event -> {
                final Packet<?> packet = event.getPacket();
                if (packet instanceof S02PacketChat) {
                    S02PacketChat s02 = (S02PacketChat) packet;
                    if (s02.getChatComponent() != null) {
                        final String formattedText = s02.getChatComponent().getUnformattedText();
                        if (formattedText.contains("was killed by")) {
                            final String[] wordsInMessage = formattedText.split(" ");
                            if (wordsInMessage[4].contains(mc.thePlayer.getName())) {
                                final String randomPolishInsult = StringUtil.randomInArray(polishInsults);
                                final String randomNormalInsult = StringUtil.randomInArray(normalInsults);
                                final String insult = String.format("/achat %s",
                                        (killInsultsModeProperty.getPropertyValue().equals(KillInsultsMode.POLSKI) ? randomPolishInsult : randomNormalInsult)
                                                .replaceAll("%s", NetworkUtil.removeRankColorCodes(wordsInMessage[0])));
                                PacketUtil.sendPacketDirect(new C01PacketChatMessage(insult));
                                Statistics.getInstance().addKill();
                            }
                        }
                        if (formattedText.toLowerCase().contains("you died")) {
                            mc.thePlayer.sendChatMessage("/play solo_insane");
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
                                mc.thePlayer.sendChatMessage("/play solo_insane");
                                AllureClient.getInstance().getNotificationManager().addNotification("Auto Hypixel",
                                        "You have been sent to a new game", 3500, NotificationType.SUCCESS);
                                if (message.toLowerCase().contains("victory"))
                                    Statistics.getInstance().addWin();
                            }
                        }
                    }
                }
            });
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
        NORMAL("Normal"),
        POLSKI("Polski");

        private final String name;

        KillInsultsMode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
