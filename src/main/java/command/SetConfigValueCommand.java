package command;

public class SetConfigValueCommand extends Command {

    /**
     * Before setting values, they have to be "opened". When using affected commands, these will take care of that on initial call.
     */
    public SetConfigValueCommand() {
        super("updatevalue");
    }
}
