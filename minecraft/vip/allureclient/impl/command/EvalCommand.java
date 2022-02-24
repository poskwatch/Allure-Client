package vip.allureclient.impl.command;


import vip.allureclient.AllureClient;
import vip.allureclient.base.command.Command;
import vip.allureclient.base.command.ArgumentException;


public class EvalCommand implements Command {

    @Override
    public String[] getAliases() {
        return new String[]{"eval", "execute", "e"};
    }

    @Override
    public void execute(String[] arguments) throws ArgumentException {
        AllureClient.getInstance().getScriptManager().runTestScript();
    }

    @Override
    public String getUsage() {
        return ".eval <file-location>";
    }
}
