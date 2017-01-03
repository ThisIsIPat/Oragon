package event;

import command.Command;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IPat on 03.01.2017.
 */
public class CommandListener extends ListenerAdapter {

    private static final String DEFAULT_COMMAND_PREFIX = "&";

    private Map<String, Command> commands;

    public CommandListener() {
        commands = new HashMap<>();
        // TODO: this(new Command1(), new Command2(), ...)
    }

    private CommandListener(Command... commands) {
        for (Command command : commands)
            this.commands.put(command.getCommandName(), command);
    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        // Just execute command
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        // Fetch guild specific prefix, then execute command
    }

    private boolean fetchAndExecuteCommand(String unformattedCommand) {
        String[] fullCommand = StringUtils.split(unformattedCommand);

        Command command = commands.get(fullCommand[0]);
        return command.onCommand(Arrays.copyOfRange(fullCommand, 1, fullCommand.length));
    }
}
