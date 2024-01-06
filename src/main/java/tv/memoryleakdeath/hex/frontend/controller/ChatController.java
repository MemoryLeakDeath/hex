package tv.memoryleakdeath.hex.frontend.controller;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import jakarta.servlet.http.HttpServletRequest;
import tv.memoryleakdeath.hex.backend.dao.channel.ChannelsDao;
import tv.memoryleakdeath.hex.backend.dao.chat.ChatMessageDao;
import tv.memoryleakdeath.hex.backend.dao.security.AuthenticationDao;
import tv.memoryleakdeath.hex.common.pojo.Channel;
import tv.memoryleakdeath.hex.common.pojo.ChatMessage;
import tv.memoryleakdeath.hex.common.pojo.ChatMessageDetails;
import tv.memoryleakdeath.hex.common.pojo.HexUser;
import tv.memoryleakdeath.hex.common.pojo.IncomingChatMessage;
import tv.memoryleakdeath.hex.frontend.utils.UserUtils;

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
        HexUser user = getUser(principal);
        if (user == null) {
            logger.error("User is not found and cannot chat on channelName: {}", channelName);
            return null;
        }
        Channel channel = channelDao.getChannelByName(channelName);
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setChannelName(channelName);
        chatMessage.setMessage(message.getMessage());
        chatMessage.setChannelId(channel.getUserId());
        chatMessage.setSenderId(user.getId());
        chatMessage.setVisible(Boolean.TRUE);
        ChatMessageDetails messageDetails = new ChatMessageDetails();
        messageDetails.setSenderDisplayName(user.getDisplayName());
        chatMessage.setDetails(messageDetails);
        ChatMessage savedMessage = messageDao.createChatMessage(chatMessage);
        return savedMessage;
    }

    @SubscribeMapping("/topic/chat/{channelName}")
    public void chatSubscribeListener(Principal principal, @DestinationVariable("channelName") String channelName) {

    }

    private HexUser getUser(Principal principal) {
        return UserUtils.getUserFromSpringPrincipal(principal);
    }

    @GetMapping("/channel/{channelName}/chat")
    public String view(HttpServletRequest request, Model model, @PathVariable("channelName") String channelName) {
        setLayout(model, "layout/minimal");
        addPageCSS(model, "/css/chat.css");
        model.addAttribute("channelName", channelName);
        return "chat/chat";
    }

}
