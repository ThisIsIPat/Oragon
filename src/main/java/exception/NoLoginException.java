package exception;

import core.Config;

import java.io.File;
import java.io.FileNotFoundException;

public class NoLoginException extends FileNotFoundException {
    
    public NoLoginException(File place) {
        super(Config.DEFAULT_LOGIN_FILE_NAME + " couldn't be found.\nPlease create the file and insert the login token.\n" +
                "(The file was searched at: " + place.getAbsolutePath() + ")");
    }
}
