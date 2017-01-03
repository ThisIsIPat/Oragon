package exception;

import java.io.FileNotFoundException;

public class NoLoginException extends FileNotFoundException {
    public NoLoginException() {
        super("login.luna couldn't be found.\nPlease create the file and insert the login token.");
    }
}
