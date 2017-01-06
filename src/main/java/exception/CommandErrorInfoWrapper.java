package exception;

public class CommandErrorInfoWrapper extends RuntimeException implements CommandErrorInfo {
    
    private final String error;
    
    public CommandErrorInfoWrapper(String error) {
        this.error = error;
    }

    public String getMessage() {
        return error;
    }
}
