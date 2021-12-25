package vip.allureclient.impl.command;

import org.lwjgl.input.Keyboard;
import vip.allureclient.AllureClient;
import vip.allureclient.base.command.Command;
import vip.allureclient.base.command.CommandArgumentException;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.util.visual.ChatUtil;

public class ToggleCommand implements Command {
    @Override
    public String[] getAliases() {
        return new String[]{"toggle", "t"};
    }

    @Override
    public void execute(String[] arguments) throws CommandArgumentException {
        if (arguments.length == 2) {
            for (Module module : AllureClient.getInstance().getModuleManager().getModules.get()) {
                if (arguments[1].equalsIgnoreCase(module.getModuleName())) {
                    module.toggle();
                    ChatUtil.sendMessageToPlayer("Toggled " + module.getModuleName());
                    return;
                }
            }
            throw new CommandArgumentException(getUsage());
        }
        throw new CommandArgumentException(getUsage());
    }

    @Override
    public String getUsage() {
        return ".toggle <module>";
    }
}
