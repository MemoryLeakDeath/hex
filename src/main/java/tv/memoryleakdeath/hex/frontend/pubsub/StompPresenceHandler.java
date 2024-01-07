package tv.memoryleakdeath.hex.frontend.pubsub;

import java.time.Instant;
import java.util.List;
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
        HexUser user = UserUtils.getUserFromSpringPrincipal(event.getUser());
        String destination = StompHeaderAccessor.wrap(event.getMessage()).getDestination();
        if (user == null) {
            logger.error("Unable to track subscribing user, user is null -- destination: {}", destination);
            return;
        }
        String sessionId = getUniqueSessionId(StompHeaderAccessor.wrap(event.getMessage()), user.getId());
        PubSubDetails details = new PubSubDetails();
        details.setDestinationTopic(destination);
        details.setSessionId(sessionId);
        details.setUser(user);
        details.setLastActivity(Instant.now());
        logger.debug("user: {} subscribed to: {}, session id: {}", user.getDisplayName(), destination, sessionId);
        topicSubscriptions.put(sessionId, details);
    }

    @EventListener
    public void handleUnsubscribe(final SessionUnsubscribeEvent event) {
        HexUser user = UserUtils.getUserFromSpringPrincipal(event.getUser());
        if (user == null) {
            logger.error("Unable to track unsubscribing user, user is null!");
            return;
        }
        String sessionId = getUniqueSessionId(StompHeaderAccessor.wrap(event.getMessage()), user.getId());
        logger.debug("Unsubscribe received, sessionId: {}", sessionId);
        topicSubscriptions.remove(sessionId);
    }

    @EventListener
    public void handleDisconnect(final SessionDisconnectEvent event) {
        HexUser user = UserUtils.getUserFromSpringPrincipal(event.getUser());
        if (user == null) {
            logger.error("Unable to track disconnecting user, user is null!");
            return;
        }
        String sessionId = getUniqueSessionId(StompHeaderAccessor.wrap(event.getMessage()), user.getId());
        logger.debug("Disconnect received, sessionId: {}", sessionId);
        topicSubscriptions.remove(sessionId);
    }

    public String getUniqueSessionId(StompHeaderAccessor accessor, String userId) {
        return accessor.getSessionId() + "-" + userId;
    }

    public List<PubSubDetails> getTopicSubscribers(String topic) {
        return topicSubscriptions.values().stream().filter(s -> s.getDestinationTopic().equals(topic)).toList();
    }

    public void updateLastAccessed(String id) {
        PubSubDetails details = topicSubscriptions.get(id);
        if (details != null) {
            details.setLastActivity(Instant.now());
        }
        topicSubscriptions.put(id, details);
    }
}
