package vip.allureclient.impl.command;

import net.minecraft.client.Minecraft;
import vip.allureclient.base.command.ArgumentException;
import vip.allureclient.base.command.Command;
import vip.allureclient.base.util.client.NetworkUtil;
import vip.allureclient.base.util.player.PlayerUtil;
import vip.allureclient.base.util.visual.ChatUtil;

public class DebugCommand implements Command {
    @Override
    public String[] getAliases() {
        return new String[] {"debug"};
    }

    @Override
    public void execute(String[] arguments) throws ArgumentException {
        ChatUtil.sendMessageToPlayer(String.format("\247lX Position:\247r %.1f", mc().thePlayer.posX));
        ChatUtil.sendMessageToPlayer(String.format("\247lY Position:\247r %.1f", mc().thePlayer.posY));
        ChatUtil.sendMessageToPlayer(String.format("\247lZ Position:\247r %.1f", mc().thePlayer.posZ));
        ChatUtil.sendMessageToPlayer(String.format("\247lGame mode:\247r %s", PlayerUtil.getGameMode()));
        ChatUtil.sendMessageToPlayer(String.format("\247lPing:\247r %d", NetworkUtil.getPing()));
        ChatUtil.sendMessageToPlayer(String.format("\247lFPS:\247r %d", Minecraft.getDebugFPS()));
    }

    @Override
    public String getUsage() {
        return null;
    }
}
