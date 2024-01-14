package tv.memoryleakdeath.hex.backend.chat;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import tv.memoryleakdeath.hex.backend.dao.channel.ChannelsDao;
import tv.memoryleakdeath.hex.backend.dao.chat.ChatMessageDao;
import tv.memoryleakdeath.hex.backend.dao.user.UserDetailsDao;
import tv.memoryleakdeath.hex.common.pojo.Channel;
import tv.memoryleakdeath.hex.common.pojo.ChannelEvent;
import tv.memoryleakdeath.hex.common.pojo.ChatMessage;
import tv.memoryleakdeath.hex.common.pojo.ChatMessageDetails;
import tv.memoryleakdeath.hex.common.pojo.HexUser;
import tv.memoryleakdeath.hex.common.pojo.IncomingChatMessage;
import tv.memoryleakdeath.hex.common.pojo.UserDetailsDisplay;
import tv.memoryleakdeath.hex.frontend.pubsub.PubSubDetails;
import tv.memoryleakdeath.hex.frontend.pubsub.StompPresenceHandler;

@Service
public class ChatService {
    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    @Autowired
    private StompPresenceHandler presenceHandler;

    @Autowired
    private ChannelsDao channelDao;

    @Autowired
    private ChatMessageDao messageDao;

    @Autowired
    private UserDetailsDao userDetailsDao;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    public void updateUserPresence(StompHeaderAccessor headerAccessor, String userId) {
        String sessionId = presenceHandler.getUniqueSessionId(headerAccessor, userId);
        presenceHandler.updateLastAccessed(sessionId);
    }

    public ChatMessage processAndSaveMessage(String channelName, HexUser sendingUser, IncomingChatMessage message) {
        Channel channel = channelDao.getChannelByName(channelName);
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setChannelName(channelName);
        chatMessage.setMessage(message.getMessage());
        chatMessage.setChannelId(channel.getUserId());
        chatMessage.setSenderId(sendingUser.getId());
        chatMessage.setVisible(Boolean.TRUE);
        ChatMessageDetails messageDetails = new ChatMessageDetails();
        messageDetails.setSenderDisplayName(sendingUser.getDisplayName());
        chatMessage.setDetails(messageDetails);
        ChatMessage savedMessage = messageDao.createChatMessage(chatMessage);
        return savedMessage;
    }

    public List<PubSubDetails> getChatViewers(String channelName) {
        String topic = "/topic/chat/%s".formatted(channelName);
        return presenceHandler.getTopicSubscribers(topic);
    }

    @EventListener
    @Async
    public void processChannelEvents(ChannelEvent event) {
        UserDetailsDisplay userDetails = userDetailsDao.getDisplayUserDetails(event.getSourceUserId());
        if (userDetails == null) {
            logger.error("Unable to find userdetails for sourceUserId: {}", event.getSourceUserId());
            return;
        }
        Channel channel = channelDao.getChannelByName(event.getChannelName());
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setChannelName(event.getChannelName());
        chatMessage.setChannelId(channel.getUserId());
        chatMessage.setVisible(Boolean.TRUE);
        ChatMessageDetails messageDetails = new ChatMessageDetails();
        messageDetails.setSenderDisplayName(userDetails.getDisplayName());
        messageDetails.setEventType(event.getType());
        chatMessage.setDetails(messageDetails);
        switch (event.getType()) {
        case FOLLOW:
            chatMessage.setMessage("%s just followed!".formatted(userDetails.getDisplayName()));
            break;
        case GIFT:
            chatMessage.setMessage("[Placeholder] A Gift Event Happened!");
            break;
        case SUBSCRIBE:
            chatMessage.setMessage("%s just subscribed!".formatted(userDetails.getDisplayName()));
            break;
        default:
            logger.error("An unknown message type was provided to processChannelEvents! Type: {}", event.getType());
            break;
        }
        String topic = "/topic/chat/%s".formatted(event.getChannelName());
        logger.debug("Sending ChatMessage: {} to topic: {}", chatMessage, topic);
        simpMessagingTemplate.convertAndSend(topic, chatMessage);
    }
}
