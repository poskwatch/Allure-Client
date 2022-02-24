package vip.allureclient.impl.command;

import org.lwjgl.input.Keyboard;
import vip.allureclient.AllureClient;
import vip.allureclient.base.command.Command;
import vip.allureclient.base.command.ArgumentException;
import vip.allureclient.base.module.Module;
import vip.allureclient.visual.notification.NotificationType;

public class BindCommand implements Command {
    @Override
    public String[] getAliases() {
        return new String[]{"bind", "b"};
    }

    @Override
    public void execute(String[] arguments) throws ArgumentException {
        if (arguments.length == 3 && arguments[2].length() == 1) {
            for (Module module : AllureClient.getInstance().getModuleManager().getModulesAsArraylist()) {
                if (arguments[1].equalsIgnoreCase(module.getModuleName())) {
                    module.setBind(Keyboard.getKeyIndex(arguments[2]));
                    AllureClient.getInstance().getNotificationManager().addNotification("Bound Module",
                            "Registered key " + arguments[2].toUpperCase() + " to toggle " + module.getModuleName(), 500, NotificationType.SUCCESS);
                    return;
                }
            }
            throw new ArgumentException(getUsage());
        }
        throw new ArgumentException(getUsage());
    }

    @Override
    public String getUsage() {
        return ".bind <module> <key>";
    }
}
