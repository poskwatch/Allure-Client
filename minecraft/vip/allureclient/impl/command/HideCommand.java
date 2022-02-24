package vip.allureclient.impl.command;

import vip.allureclient.AllureClient;
import vip.allureclient.base.command.Command;
import vip.allureclient.base.command.ArgumentException;
import vip.allureclient.base.module.Module;
import vip.allureclient.visual.notification.NotificationType;

public class HideCommand implements Command {
    @Override
    public String[] getAliases() {
        return new String[]{"hide", "h"};
    }

    @Override
    public void execute(String[] arguments) throws ArgumentException {
        if (arguments.length == 2) {
            for (Module module : AllureClient.getInstance().getModuleManager().getModulesAsArraylist()) {
                if (arguments[1].equalsIgnoreCase(module.getModuleName().replaceAll(" ", ""))) {
                    module.setHidden(!module.isHidden());
                    AllureClient.getInstance().getNotificationManager().addNotification("Module Hidden",
                            "Module " + module.getModuleName() + " is now " +
                            (module.isHidden() ? "hidden" : "unhidden"), 1000, NotificationType.SUCCESS);
                    return;
                }
            }
            throw new ArgumentException(getUsage());
        }
        throw new ArgumentException(getUsage());
    }

    @Override
    public String getUsage() {
        return ".hide <module>";
    }
}
