package event;

import command.Command;
import command.RoomLotteryCommand;
import core.Config;
import net.dv8tion.jda.core.events.message.GenericMessageEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Created by IPat on 03.01.2017.
 */
public class CommandListener extends ListenerAdapter {

    private static final String DEFAULT_COMMAND_PREFIX = "&";
    private static final String COMMAND_ERROR_MSG = "Sorry, the command couldn't be executed.";
    private static final int COMMAND_CLEANUP_TIMER = 5000;

    private Map<String, Command> commands;
    private Set<String> permittedUsers;

    public CommandListener() {
        // TODO: this(new Command1(), new Command2(), ...)
        this(new RoomLotteryCommand());
    }

    private CommandListener(Command... commands) {
        this.commands = new HashMap<>();
        this.permittedUsers = new HashSet<>();
        permittedUsers.add("147024562817597440");
        permittedUsers.add("113355064659152896");
        for (Command command : commands)
            this.commands.put(command.getCommandName(), command);
    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        if (!permittedUsers.contains(event.getAuthor().getId()) && !event.getAuthor().getId().equals(event.getJDA().getSelfUser().getId())) {
            if (!event.getAuthor().hasPrivateChannel())
                event.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("milad, this bot is still in development and can only be accessed by milad IPat. Talk to him if you want access.").queue());
            else
                event.getAuthor().getPrivateChannel().sendMessage("milad, this bot is still in development and can only be accessed by milad IPat. Talk to him if you want access.").queue();
            return;
        }
        // Execute command
        if (!fetchAndExecuteCommand(event, event.getMessage().getRawContent()))
            event.getChannel().sendMessage(COMMAND_ERROR_MSG).queue();
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        // Fetch guild specific prefix
        final String guildId = event.getGuild().getId();
        String guildSpecificCmdPrefix = Config.get(event.getGuild(), "__"+guildId+"_CMDPREFIX");
        
        if (guildSpecificCmdPrefix == null) {
            Config.put(event.getGuild(), "__"+guildId+"_CMDPREFIX", DEFAULT_COMMAND_PREFIX);
            guildSpecificCmdPrefix = DEFAULT_COMMAND_PREFIX;
        }
        
        if (event.getMessage().getRawContent().startsWith(guildSpecificCmdPrefix)) {
            if (!permittedUsers.contains(event.getAuthor().getId())) {
                if (!event.getAuthor().hasPrivateChannel())
                    event.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("milad, this bot is still in development and can only be accessed by milad IPat. Talk to him if you want access.").queue());
                else
                    event.getAuthor().getPrivateChannel().sendMessage("milad, this bot is still in development and can only be accessed by milad IPat. Talk to him if you want access.").queue();
                return;
            }
            StringBuilder commandBuilder = new StringBuilder(event.getMessage().getRawContent());
            commandBuilder.delete(0, guildSpecificCmdPrefix.length());
            if (!fetchAndExecuteCommand(event, commandBuilder.toString()))
                if (event.getChannel().canTalk())
                    event.getChannel().sendMessage(COMMAND_ERROR_MSG).queue();
            Thread commandCleaner = new Thread(() -> {
                try {
                    Thread.sleep(COMMAND_CLEANUP_TIMER);
                    event.getMessage().deleteMessage().queue();
                } catch (InterruptedException e) {
                    System.out.println("commandCleaner interrupted on waiting for cleanup");
                }
            });
            commandCleaner.start();
        }
    }

    private boolean fetchAndExecuteCommand(GenericMessageEvent event, String unformattedCommand) {
        String[] fullCommand = StringUtils.split(unformattedCommand);

        Command command = commands.get(fullCommand[0].toLowerCase());
        return command == null || command.triggerCommand(event, Arrays.copyOfRange(fullCommand, 1, fullCommand.length));
    }
}
