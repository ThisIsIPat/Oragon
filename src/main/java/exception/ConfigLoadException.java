package exception;

public class ConfigLoadException extends Exception {

    private static final String DEFAULT_MSG = "The config isn't loaded properly.";

    public ConfigLoadException() {
        this(DEFAULT_MSG);
    }

    public ConfigLoadException(String msg) {
        super("Message: " + msg);
    }

    public ConfigLoadException(String msg, String key) {
        this(msg + " (Tried accessing value with key: " + key + ")");
    }

    public ConfigLoadException(Throwable throwable) {
        this(DEFAULT_MSG, throwable);
    }

    public ConfigLoadException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

    public ConfigLoadException(String msg, String key, Throwable throwable) {
        this(msg + " (Tried accessing value with key: " + key + ")", throwable);
    }

}
