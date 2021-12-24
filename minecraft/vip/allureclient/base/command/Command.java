package vip.allureclient.base.command;

public interface Command {
    String[] getAliases();
    void execute(String[] arguments) throws CommandArgumentException;
    String getUsage();
}
