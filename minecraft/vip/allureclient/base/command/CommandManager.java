package vip.allureclient.base.command;

import net.minecraft.util.ChatComponentText;
import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.impl.command.*;
import vip.allureclient.impl.event.player.ChatMessageSendEvent;

import java.util.ArrayList;

public class CommandManager {

    private final String commandPrefix = ".";

    @EventListener
    EventConsumer<ChatMessageSendEvent> onSendChatMessageEvent;

    private final ArrayList<Command> commands = new ArrayList<>();

    public CommandManager() {
        this.onSendChatMessageEvent = (chatEvent -> {
            if (chatEvent.getMessage().startsWith(commandPrefix)) {
                chatEvent.setCancelled(true);
                String afterPrefix = chatEvent.getMessage().substring(1);
                String[] afterPrefixSplit = afterPrefix.split(" ");
                for (Command command : commands) {
                    for (String alias : command.getAliases()) {
                        if (afterPrefixSplit[0].equalsIgnoreCase(alias)) {
                            try {
                                command.execute(afterPrefixSplit);
                            }
                            catch (CommandArgumentException e) {
                                Wrapper.getPlayer().addChatMessage(new ChatComponentText(e.getMessage()));
                            }
                            break;
                        }
                    }
                }
            }
        });
        registerCommands();
        Wrapper.getEventManager().subscribe(this);
    }

    private void registerCommands() {
        commands.add(new ConfigCommand());
        commands.add(new VClipCommand());
        commands.add(new ToggleCommand());
        commands.add(new BindCommand());
        commands.add(new HideCommand());
        commands.add(new RenameCommand());
        commands.add(new EvalCommand());
    }

}
