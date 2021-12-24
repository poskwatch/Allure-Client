package vip.allureclient.base.command;

public class CommandArgumentException extends Exception {

    public CommandArgumentException(String message) {
        super("Invalid Arguments. Usage: " + message);
    }

}
