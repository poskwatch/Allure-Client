package vip.allureclient.impl.command;

import vip.allureclient.AllureClient;
import vip.allureclient.base.command.Command;
import vip.allureclient.base.command.ArgumentException;
import vip.allureclient.visual.notification.NotificationType;

public class ConfigCommand implements Command {
    @Override
    public String[] getAliases() {
        return new String[]{"c", "config", "cfg", "profile", "settings"};
    }

    @Override
    public void execute(String[] arguments) throws ArgumentException {
        if (arguments.length == 3) {
            if (arguments[1].equalsIgnoreCase("save")) {
                if (AllureClient.getInstance().getConfigManager().saveConfig(arguments[2]))
                    AllureClient.getInstance().getNotificationManager().addNotification("Config saved", "Successfully saved config " + arguments[2],
                            1500, NotificationType.SUCCESS);
                else
                    AllureClient.getInstance().getNotificationManager().addNotification("Couldn't save config", "Unable to save config " + arguments[2],
                            1500, NotificationType.ERROR);
                return;
            }
            else if (arguments[1].equalsIgnoreCase("load")) {
                if (AllureClient.getInstance().getConfigManager().loadConfig(arguments[2]))
                    AllureClient.getInstance().getNotificationManager().addNotification("Config loaded", "Successfully loaded config " + arguments[2],
                            1500, NotificationType.SUCCESS);
                else
                    AllureClient.getInstance().getNotificationManager().addNotification("Couldn't load config", "Unable to load config config " + arguments[2],
                            1500, NotificationType.ERROR);
                return;
            } else {
                throw new ArgumentException(getUsage());
            }
        }
        throw new ArgumentException(getUsage());
    }

    @Override
    public String getUsage() {
        return ".config <save/load> <config-name>";
    }
}
