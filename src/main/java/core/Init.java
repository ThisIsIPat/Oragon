package core;

import event.CommandListener;
import exception.ConfigLoadException;
import exception.NoLoginException;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import javax.security.auth.login.LoginException;

public class Init {

    private static JDA jda;

    public static void main(String[] args) {
        System.out.println("OragonBot is starting up...");

        // Create configuration file system...

        System.out.println(Config.getHostingFolder());

        final String LOGIN_TOKEN;
        try {
            Config.loadConfig();
            LOGIN_TOKEN = Config.getLoginToken();
        } catch (ConfigLoadException e) {
            throw new RuntimeException("There was an unexpected error loading up the login token for the bot!", e);
        } catch (NoLoginException e) {
            throw new RuntimeException(e);
        }

        try {
            jda = new JDABuilder(AccountType.BOT).setToken(LOGIN_TOKEN).buildAsync();
            jda.addEventListener(new CommandListener());
        } catch (LoginException e) {
            // Anything could be at fault here /shrug
            e.printStackTrace();
        } catch (RateLimitedException e) {
            // Don't login that frequent! Wait a bit until the next time...
            e.printStackTrace();
        }
    }

}
