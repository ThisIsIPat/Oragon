package event;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A session gets opened when input of one user needs to be handled and closed once all queued output is received.
 */
public class Session {
    
    private static Map<User, Session> sessionMap;
    
    public interface SessionLink {
        Session getSession();
    }
    
    private final User user;
    private final MessageChannel channel;
    private final List<InputListener.ResponseHandler> handlers;
    
    public static void queueQuestions(User user, MessageChannel channel, InputListener.ResponseHandler... handlers) {
        
        if (sessionMap == null) sessionMap = new HashMap<>();
        
        if (user != null && handlers.length > 0) {
            if (sessionMap.containsKey(user))
                sessionMap.get(user).addHandlers(handlers);
            else {
                sessionMap.put(user, new Session(user, channel, Arrays.asList(handlers)));
                sessionMap.get(user).queueInput();
            }
        }
    }
    
    private Session(User user, MessageChannel channel, List<InputListener.ResponseHandler> handlers) {
        this.user = user;
        this.handlers = handlers;
        this.channel = channel;
    }
    
    public void addHandlers(InputListener.ResponseHandler... handlers) {
        this.handlers.addAll(Arrays.asList(handlers));
    }
    
    private InputListener listenerCache;
    
    private void queueInput() {
        if (handlers.size() <= 0) {
            sessionMap.remove(user);
            return;
        }
        InputListener listener = new InputListener(user, channel, () -> Session.this);
        listenerCache = listener;
        user.getJDA().addEventListener(listener);
    }
    
    public void handleMessage(Message message) {
        if (handlers.get(0).receive(message)) {
            handlers.remove(0);
            user.getJDA().removeEventListener(listenerCache);
            queueInput();
        }
    }
    
}