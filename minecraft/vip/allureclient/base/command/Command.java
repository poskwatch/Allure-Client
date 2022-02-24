package vip.allureclient.base.command;

import net.minecraft.client.Minecraft;

public interface Command {
    default Minecraft mc() {
        return Minecraft.getMinecraft();
    }
    String[] getAliases();
    void execute(String[] arguments) throws ArgumentException;
    String getUsage();
}
