package event;

import command.Command;
import core.Config;
import net.dv8tion.jda.core.events.message.GenericMessageEvent;
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
    private static final String COMMAND_ERROR_MSG = "Sorry, the command couldn't be executed.";

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
        // Execute command
        if (!fetchAndExecuteCommand(event, event.getMessage().getRawContent()))
            event.getChannel().sendMessage(COMMAND_ERROR_MSG);
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        // Fetch guild specific prefix
        final String guildId = event.getGuild().getId();
        String guildSpecificCmdPrefix = Config.get(String.format("__%1$s_CMDPREFIX", guildId));
        
        if (guildSpecificCmdPrefix == null) {
            Config.put(String.format("__%1$s_CMDPREFIX", guildId), DEFAULT_COMMAND_PREFIX);
            guildSpecificCmdPrefix = DEFAULT_COMMAND_PREFIX;
        }
        
        if (event.getMessage().getRawContent().startsWith(guildSpecificCmdPrefix)) {
            StringBuilder commandBuilder = new StringBuilder(event.getMessage().getRawContent());
            commandBuilder.delete(0, guildSpecificCmdPrefix.length());
            if (!fetchAndExecuteCommand(event, commandBuilder.toString()))
                if (event.getChannel().canTalk())
                    event.getChannel().sendMessage(COMMAND_ERROR_MSG);
        }
    }

    private boolean fetchAndExecuteCommand(GenericMessageEvent event, String unformattedCommand) {
        String[] fullCommand = StringUtils.split(unformattedCommand);

        Command command = commands.get(fullCommand[0].toLowerCase());
        return command.triggerCommand(event, Arrays.copyOfRange(fullCommand, 1, fullCommand.length));
    }
}
