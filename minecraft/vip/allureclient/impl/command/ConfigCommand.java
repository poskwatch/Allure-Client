package vip.allureclient.impl.command;

import vip.allureclient.AllureClient;
import vip.allureclient.base.command.Command;
import vip.allureclient.base.command.CommandArgumentException;
import vip.allureclient.base.util.visual.ChatUtil;

public class ConfigCommand implements Command {
    @Override
    public String[] getAliases() {
        return new String[]{"c", "config", "cfg", "profile", "settings"};
    }

    @Override
    public void execute(String[] arguments) throws CommandArgumentException {
        if (arguments.length == 3) {
            if (arguments[1].equalsIgnoreCase("save")) {
                if (AllureClient.getInstance().getConfigManager().saveConfig(arguments[2]))
                    ChatUtil.sendMessageToPlayer("Saved config " + arguments[2]);
                else
                    ChatUtil.sendMessageToPlayer("Couldn't save config " + arguments[2]);
                return;
            }
            else if (arguments[1].equalsIgnoreCase("load")) {
                if (AllureClient.getInstance().getConfigManager().loadConfig(arguments[2]))
                    ChatUtil.sendMessageToPlayer("Loaded config " + arguments[2]);
                else
                    ChatUtil.sendMessageToPlayer("Couldn't load config " + arguments[2]);
                return;
            } else {
                throw new CommandArgumentException(getUsage());
            }
        }
        throw new CommandArgumentException(getUsage());
    }

    @Override
    public String getUsage() {
        return ".config <save/load> <config-name>";
    }
}
