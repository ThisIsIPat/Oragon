package exception;

public class ArgumentAmountException extends IllegalArgumentException implements CommandErrorInfo {

    public enum Type {
        NOT_ENOUGH,
        TOO_MANY
    }
    
    private final Type type;
    
    public ArgumentAmountException(Type type) {
        this.type = type;
    }
    
    public Type getType() {
        return this.type;
    }
    
    @Override
    public String getMessage() {
        switch(type) {
            case NOT_ENOUGH:
                return "Not enough arguments given for executing command!";
            case TOO_MANY:
                return "Too many arguments given for executing command!";
            default:
                return null;
        }
    }
}
