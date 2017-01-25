package event;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class InputListener extends ListenerAdapter {
    
    public interface ResponseHandler {
        boolean receive(Message response);
    }
    
    private final Session.SessionLink sessionLink;
    private final User user;
    private final MessageChannel channel;
    
    InputListener(User user, MessageChannel channel, Session.SessionLink sessionLink) {
        this.sessionLink = sessionLink;
        this.user = user;
        this.channel = channel;
    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        if (event.getAuthor().equals(this.user) && event.getChannel().equals(channel)) {
            sessionLink.getSession().handleMessage(event.getMessage());
        }
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getAuthor().equals(this.user) && event.getChannel().equals(channel)) {
            sessionLink.getSession().handleMessage(event.getMessage());
        }
    }
    
}
