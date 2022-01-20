package vip.allureclient.impl.command;

import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import vip.allureclient.AllureClient;
import vip.allureclient.base.command.Command;
import vip.allureclient.base.command.CommandArgumentException;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.visual.notification.NotificationType;

public class MemeCommand implements Command {
    @Override
    public String[] getAliases() {
        return new String[] {"meme"};
    }

    @Override
    public void execute(String[] arguments) throws CommandArgumentException {
        C13PacketPlayerAbilities c13 = new C13PacketPlayerAbilities();
        c13.setAllowFlying(true);
        c13.setFlying(true);
        Wrapper.sendPacketDirect(c13);
        AllureClient.getInstance().getNotificationManager().addNotification("Memed", "C13 Packet did meme", 5000, NotificationType.SUCCESS);
    }

    @Override
    public String getUsage() {
        return ".meme";
    }
}
