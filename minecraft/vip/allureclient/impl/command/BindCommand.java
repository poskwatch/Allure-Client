package vip.allureclient.impl.command;

import org.lwjgl.input.Keyboard;
import vip.allureclient.AllureClient;
import vip.allureclient.base.command.Command;
import vip.allureclient.base.command.CommandArgumentException;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.base.util.visual.ChatUtil;

public class BindCommand implements Command {
    @Override
    public String[] getAliases() {
        return new String[]{"bind", "b"};
    }

    @Override
    public void execute(String[] arguments) throws CommandArgumentException {
        if (arguments.length == 3 && arguments[2].length() == 1) {
            for (Module module : AllureClient.getInstance().getModuleManager().getModules.get()) {
                if (arguments[1].equalsIgnoreCase(module.getModuleName())) {
                    module.setBind(Keyboard.getKeyIndex(arguments[2]));
                    ChatUtil.sendMessageToPlayer("Bound " + module.getModuleName() + " to key " + arguments[2]);
                    return;
                }
            }
            throw new CommandArgumentException(getUsage());
        }
        throw new CommandArgumentException(getUsage());
    }

    @Override
    public String getUsage() {
        return ".bind <module> <key>";
    }
}
