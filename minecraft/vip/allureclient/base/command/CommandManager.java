package vip.allureclient.base.command;

import io.github.poskwatch.eventbus.api.annotations.EventHandler;
import io.github.poskwatch.eventbus.api.enums.Priority;
import io.github.poskwatch.eventbus.api.interfaces.IEventCallable;
import io.github.poskwatch.eventbus.api.interfaces.IEventListener;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.impl.command.*;
import vip.allureclient.impl.event.events.player.ChatMessageSendEvent;

import java.util.ArrayList;
import java.util.Arrays;

public class CommandManager {

    // Prefix all commands must start with
    private final String PREFIX = ".";

    // Map storing command aliases to their instances
    private final static ArrayList<Command> commandStorage = new ArrayList<>();

    public CommandManager() {
        Wrapper.getEventBus().registerListener(new IEventListener() {
            @EventHandler(events = ChatMessageSendEvent.class, priority = Priority.HIGH)
            final IEventCallable<ChatMessageSendEvent> onSendChatMessage = (event -> {
                // If chat message has pre-fix, it's a command
                if (event.getMessage().startsWith(PREFIX)) {
                    // Cancel the event so the message isn't sent
                    event.setCancelled(true);
                    // Create split string array after prefix by spaces
                    final String[] formattedCommand = event.getMessage().substring(1).split(" ");
                    // Iterate through command aliases checking if it matches
                    for (Command command : commandStorage) {
                        for (String commandAlias : command.getAliases()) {
                            // Check if first element is an alias, ignoring case iteration
                            if (formattedCommand[0].equalsIgnoreCase(commandAlias)) {
                                try {
                                    command.execute(formattedCommand);
                                } catch (ArgumentException e) {
                                    // If arguments are incorrect, throws exception
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            });
        });
    }

    // Register commands into storage via addAll
    public static void registerCommands() {
        commandStorage.add(new ConfigCommand());
        commandStorage.add(new VClipCommand());
        commandStorage.add(new ToggleCommand());
        commandStorage.add(new BindCommand());
        commandStorage.add(new HideCommand());
        commandStorage.add(new EvalCommand());
        commandStorage.add(new DebugCommand());
    }
}
