package vip.allureclient.base.command;

public class ArgumentException extends Exception {

    public ArgumentException(String message) {
        super("Invalid Arguments. Usage: " + message);
    }

}
