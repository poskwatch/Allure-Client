package vip.allureclient.impl.command;

import vip.allureclient.AllureClient;
import vip.allureclient.base.command.Command;
import vip.allureclient.base.command.ArgumentException;
import vip.allureclient.base.module.Module;

public class ToggleCommand implements Command {
    @Override
    public String[] getAliases() {
        return new String[]{"toggle", "t"};
    }

    @Override
    public void execute(String[] arguments) throws ArgumentException {
        if (arguments.length == 2) {
            final Module module = AllureClient.getInstance().getModuleManager().getModuleOrNull(arguments[1]);
            if (module != null) {
                module.toggle();
                return;
            }
            throw new ArgumentException(getUsage());
        }
        throw new ArgumentException(getUsage());
    }

    @Override
    public String getUsage() {
        return ".toggle <module>";
    }
}
