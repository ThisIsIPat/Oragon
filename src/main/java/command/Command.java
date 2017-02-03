package command;

import exception.ArgumentAmountException;
import exception.CommandErrorInfoWrapper;
import jdk.nashorn.internal.runtime.regexp.joni.exception.SyntaxException;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.GenericMessageEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;

public abstract class Command {

    private final String commandName;
    
    private final int minArgs;
    private final int maxArgs;
    
    public Command(String commandName) {
        this(commandName, 0, -1);
    }
    
    public Command(String commandName, int maxArgs) {
        this(commandName, 0, maxArgs);
    }

    public Command(String commandName, int minArgs, int maxArgs) {
        this.commandName = commandName;
        
        this.minArgs = minArgs;
        this.maxArgs = maxArgs;
    }

    public final String getCommandName() {
        return this.commandName;
    }
    
    public final boolean triggerCommand(GenericMessageEvent event, String[] arguments) {
        if (arguments.length < minArgs)
            throw new ArgumentAmountException(ArgumentAmountException.Type.NOT_ENOUGH);
        if (maxArgs != -1 && arguments.length > maxArgs)
            throw new ArgumentAmountException(ArgumentAmountException.Type.TOO_MANY);
        // TODO: catch in upper blocks

        try {
            if (event instanceof GuildMessageReceivedEvent) {
                return onGuildCommand((GuildMessageReceivedEvent) event, arguments);
            }
            else if (event instanceof PrivateMessageReceivedEvent) {
                return onPrivateCommand((PrivateMessageReceivedEvent) event, arguments);
            } else {
                return false;
            }
        } catch (CommandErrorInfoWrapper error) {
            event.getMessage().getChannel().sendMessage("There has been an error executing the command: "+error.getMessage()).queue();
            return false;
        }
    }

    protected boolean onGuildCommand(GuildMessageReceivedEvent event, String[] arguments) throws CommandErrorInfoWrapper {
        if (event.getChannel().canTalk())
            event.getChannel().sendMessage("This command can't be used in a guild chat!").queue();
        return false;
    }

    protected boolean onPrivateCommand(PrivateMessageReceivedEvent event, String[] arguments) throws CommandErrorInfoWrapper {
        event.getChannel().sendMessage("This command can't be used in a private chat!").queue();
        return false;
    }
    
    protected final int getMinArguments() {
        return this.minArgs;
    }
    
    protected final int getMaxArguments() {
        return this.maxArgs;
    }
    
    public final void printHelp(User user) {
        CommandHelp commandHelp = getHelp();
        if (!user.hasPrivateChannel())
            user.openPrivateChannel().queue(privateChannel -> {
                privateChannel.sendMessage("**"+commandHelp.getSyntax()+"**").queue();
                privateChannel.sendMessage(commandHelp.getDescription()).queue();
                if (commandHelp.getAdditionalInfo() != null)
                    privateChannel.sendMessage("*"+commandHelp.getAdditionalInfo()+"*").queue();
            });
        else {
            user.getPrivateChannel().sendMessage("**" + commandHelp.getSyntax() + "**").queue();
            user.getPrivateChannel().sendMessage(commandHelp.getDescription()).queue();
            if (commandHelp.getAdditionalInfo() != null)
                user.getPrivateChannel().sendMessage("*" + commandHelp.getAdditionalInfo() + "*").queue();
        }
    }
    
    protected abstract CommandHelp getHelp();
    
    protected static class CommandHelp {

        public String getSyntax() {
            return syntax;
        }

        public String getDescription() {
            return description;
        }

        public String getAdditionalInfo() {
            return additionalInfo;
        }

        private final String syntax;
        private final String description;
        private final String additionalInfo;
        
        public CommandHelp(String syntax, String description, String additionalInfo) {
            if (syntax == null || description == null)
                throw new IllegalArgumentException("No syntax or description provided");
            this.syntax = syntax;
            this.description = description;
            this.additionalInfo = additionalInfo;
        }
    }

}
