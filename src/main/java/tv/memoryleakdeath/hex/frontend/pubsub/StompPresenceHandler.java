package tv.memoryleakdeath.hex.frontend.pubsub;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import tv.memoryleakdeath.hex.common.pojo.HexUser;
import tv.memoryleakdeath.hex.frontend.utils.UserUtils;

@Component
public class StompPresenceHandler {
    private static final Logger logger = LoggerFactory.getLogger(StompPresenceHandler.class);

    private static ConcurrentHashMap<String, PubSubDetails> topicSubscriptions = new ConcurrentHashMap<>();
    
    @EventListener
    public void handleSubscribe(final SessionSubscribeEvent event) {
        String sessionId = getSessionId(StompHeaderAccessor.wrap(event.getMessage()));
        String destination = StompHeaderAccessor.wrap(event.getMessage()).getDestination();
        HexUser user = UserUtils.getUserFromSpringPrincipal(event.getUser());
        PubSubDetails details = new PubSubDetails();
        details.setDestinationTopic(destination);
        details.setSessionId(sessionId);
        details.setUser(user);
        logger.debug("user: {} subscribed to: {}, session id: {}", user.getDisplayName(), destination, sessionId);
        topicSubscriptions.put(sessionId, details);
    }

    @EventListener
    public void handleUnsubscribe(final SessionUnsubscribeEvent event) {
        String sessionId = getSessionId(StompHeaderAccessor.wrap(event.getMessage()));
        logger.debug("Unsubscribe received, sessionId: {}", sessionId);
        topicSubscriptions.remove(sessionId);
    }

    @EventListener
    public void handleDisconnect(final SessionDisconnectEvent event) {
        String sessionId = getSessionId(StompHeaderAccessor.wrap(event.getMessage()));
        logger.debug("Disconnect received, sessionId: {}", sessionId);
        topicSubscriptions.remove(sessionId);
    }

    protected String getSessionId(StompHeaderAccessor accessor) {
        return accessor.getSessionId();
    }
}
