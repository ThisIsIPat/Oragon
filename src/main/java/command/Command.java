package command;

import exception.ArgumentAmountException;
import exception.CommandErrorInfoWrapper;
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

    public String getCommandName() {
        return this.commandName;
    }
    
    public boolean triggerCommand(GenericMessageEvent event, String[] arguments) {
        if (arguments.length < minArgs)
            throw new ArgumentAmountException(ArgumentAmountException.Type.NOT_ENOUGH);
        if (maxArgs != -1 && arguments.length > maxArgs)
            throw new ArgumentAmountException(ArgumentAmountException.Type.TOO_MANY);
        
        try {
            if (event instanceof GuildMessageReceivedEvent)
                return onGuildCommand((GuildMessageReceivedEvent) event, arguments);
            else if (event instanceof PrivateMessageReceivedEvent)
                return onPrivateCommand((PrivateMessageReceivedEvent) event, arguments);
            else
                return false;
        } catch (CommandErrorInfoWrapper error) {
            event.getMessage().getChannel().sendMessage("There has been an error executing the command: Lol "+error.getMessage());
            return false;
        }
    }

    protected boolean onGuildCommand(GuildMessageReceivedEvent event, String[] arguments) {
        if (event.getChannel().canTalk())
            event.getChannel().sendMessage("This command can't be used in a guild chat!");
        return false;
    }

    protected boolean onPrivateCommand(PrivateMessageReceivedEvent event, String[] arguments) {
        event.getChannel().sendMessage("This command can't be used in a private chat!");
        return false;
    }
    
    protected int getMinArguments() {
        return this.minArgs;
    }
    
    protected int getMaxArguments() {
        return this.maxArgs;
    }

}
