package vip.allureclient.impl.command;

import vip.allureclient.AllureClient;
import vip.allureclient.base.command.Command;
import vip.allureclient.base.command.CommandArgumentException;
import vip.allureclient.visual.notification.NotificationType;

public class RenameCommand implements Command {
    @Override
    public String[] getAliases() {
        return new String[]{"clientname", "rename", "r", "cn"};
    }

    @Override
    public void execute(String[] arguments) throws CommandArgumentException {
        if (arguments.length == 2) {
            AllureClient.getInstance().CUSTOM_CLIENT_NAME = arguments[1];
            AllureClient.getInstance().getNotificationManager().addNotification("Client name",
                    "Changed client name to " + arguments[1], 1000, NotificationType.SUCCESS);
            return;
        }
        throw new CommandArgumentException(getUsage());
    }

    @Override
    public String getUsage() {
        return ".clientname <name>";
    }
}
