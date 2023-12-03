package tv.memoryleakdeath.hex.frontend.controller;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;
import tv.memoryleakdeath.hex.backend.dao.channel.ChannelsDao;
import tv.memoryleakdeath.hex.backend.dao.chat.ChatMessageDao;
import tv.memoryleakdeath.hex.backend.dao.security.AuthenticationDao;
import tv.memoryleakdeath.hex.backend.security.HexRememberMeToken;
import tv.memoryleakdeath.hex.common.pojo.Auth;
import tv.memoryleakdeath.hex.common.pojo.Channel;
import tv.memoryleakdeath.hex.common.pojo.ChatMessage;
import tv.memoryleakdeath.hex.common.pojo.HexUser;
import tv.memoryleakdeath.hex.common.pojo.IncomingChatMessage;

@Controller
public class ChatController extends BaseFrontendController {
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @Autowired
    private AuthenticationDao authDao;

    @Autowired
    private ChannelsDao channelDao;

    @Autowired
    private ChatMessageDao messageDao;

    @MessageMapping("/{channelName}/sendChatMessage")
    @SendTo("/topic/chat/{channelName}")
    public ChatMessage sendChatMessage(Principal principal, IncomingChatMessage message,
            @DestinationVariable("channelName") String channelName) {
        Auth auth = getUser(principal);
        Channel channel = channelDao.getChannelByName(channelName);
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setChannelName(channelName);
        chatMessage.setMessage(message.getMessage());
        chatMessage.setChannelId(channel.getUserId());
        chatMessage.setSenderId(auth.getId());
        chatMessage.setVisible(Boolean.TRUE);
        ChatMessage savedMessage = messageDao.createChatMessage(chatMessage);
        return savedMessage;
    }

    private Auth getUser(Principal principal) {
        if (principal instanceof HexRememberMeToken) {
            HexUser token = (HexUser) ((HexRememberMeToken) principal).getPrincipal();
            return authDao.getUserByUsername(token.getUsername());
        }
        return null;
    }

    @GetMapping("/{channelName}/chat")
    public String testMethod(HttpServletRequest request, Model model) {
        setLayout(model, "layout/main");
        return "test";
    }

}
