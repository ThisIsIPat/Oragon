package core;

import exception.ConfigLoadException;
import exception.NoLoginException;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import org.nustaq.serialization.FSTConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Stores and loads configuration for the LunaBot
 */
public class Config {
    private Config() {}

    public static final String DEFAULT_CONFIG_FILE_NAME = "config.oragon";
    public static final String DEFAULT_LOGIN_FILE_NAME = "login.oragon";

    public static final String LOGIN_KEY = "__LOGIN_TOKEN";

    private static FSTConfiguration serializer = FSTConfiguration.createDefaultConfiguration();

    private static Map<String, String> cache;

    /**
     * Gives the path the bot is located in.
     * @return Path to compiled jar file
     */
    public static File getHostingFolder() {
        try {
            return new File(Config.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
        } catch (Exception e) {
            throw new RuntimeException("Couldn't resolve path.", e);
        }
    }

    public static BufferedSource initFileRead(File file) throws IOException {
        if (file.createNewFile())
            ; // File got created
        return Okio.buffer(Okio.source(file));
    }

    public static BufferedSink initFileWrite(File file) throws IOException {
        if (file.createNewFile())
            ; // File got created
        return Okio.buffer(Okio.sink(file));
    }

    private static File getConfigFile() {
        return new File(getHostingFolder(), DEFAULT_CONFIG_FILE_NAME);
    }

    private static File getLoginFile() {
        return new File(getHostingFolder(), DEFAULT_LOGIN_FILE_NAME);
    }

    public static void storeConfig() throws ConfigLoadException {
        try {
            final BufferedSink fileBuffer = initFileWrite(getConfigFile());
            cache = (HashMap<String, String>) serializer.asObject(serializer.asByteArray(cache));
            fileBuffer.write(serializer.asByteArray(cache));
            fileBuffer.flush();
            fileBuffer.close();
        } catch (IOException e) {
            throw new ConfigLoadException("Couldn't create config file", e);
        }
    }

    public static void loadConfig() throws ConfigLoadException {
        try {
            final BufferedSource fileBuffer = initFileRead(getConfigFile());

            if (getConfigFile().length() == 0)
                cache = new HashMap<>();
            else
                cache = (HashMap<String, String>) serializer.asObject(fileBuffer.readByteArray());

            fileBuffer.close();
        } catch (IOException e) {
            throw new ConfigLoadException("Couldn't create config file", e);
        }
    }

    public static String getLoginToken() throws ConfigLoadException, NoLoginException {
        if (cache == null) throw new ConfigLoadException();
        if (!getLoginFile().exists()) {
            if (cache.containsKey(LOGIN_KEY))
                return cache.get(LOGIN_KEY);
            else throw new NoLoginException(getLoginFile());
        } else {
            try {
                final BufferedSource fileBuffer = initFileRead(getLoginFile());
                final String loginToken = fileBuffer.readUtf8();
                fileBuffer.close();

                cache.put(LOGIN_KEY, loginToken);

                Config.storeConfig();

                System.out.println("Login key successfully read and stored from login.oragon into config.oragon.");

                if (!getLoginFile().delete())
                    System.out.println("login.oragon couldn't be removed. Please remove the file for better performance.");
                
                return loginToken;
            } catch (IOException e) {
                throw new ConfigLoadException("Couldn't create config file", e);
            }
        }
    }

    public static void put(String key, String value) {
        cache.put(key, value);
    }

    public static String get(String key) {
        return cache.get(key);
    }
}