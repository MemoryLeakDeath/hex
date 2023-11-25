package tv.memoryleakdeath.hex.frontend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import tv.memoryleakdeath.hex.common.pojo.ChatMessage;
import tv.memoryleakdeath.hex.common.pojo.IncomingChatMessage;

@Controller
public class ChatController {
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @MessageMapping("/{channelName}/sendChatMessage")
    @SendTo("/topic/chat/{channelName}")
    public ChatMessage sendChatMessage(IncomingChatMessage message, @DestinationVariable String channelName) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setChannelName(channelName);
        chatMessage.setMessage(message.getMessage());
        return chatMessage;
    }

}
