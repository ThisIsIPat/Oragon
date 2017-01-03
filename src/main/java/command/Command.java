package command;

import java.util.List;

public abstract class Command {

    private String commandName;

    public Command(String commandName) {
        this.commandName = commandName;
    }

    public String getCommandName() {
        return this.commandName;
    }

    public abstract boolean onCommand(String[] arguments);

}
