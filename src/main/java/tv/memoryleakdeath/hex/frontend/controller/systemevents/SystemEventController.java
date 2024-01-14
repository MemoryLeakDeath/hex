package tv.memoryleakdeath.hex.frontend.controller.systemevents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import tv.memoryleakdeath.hex.common.ChannelEventType;
import tv.memoryleakdeath.hex.common.pojo.ChannelEvent;
import tv.memoryleakdeath.hex.common.pojo.ChannelSubGiftEvent;
import tv.memoryleakdeath.hex.frontend.controller.BaseFrontendController;

@Controller
public class SystemEventController extends BaseFrontendController {
    private static final Logger logger = LoggerFactory.getLogger(SystemEventController.class);

    @Autowired
    private ApplicationContext applicationContext;

    @PostMapping("/event/sendFollowEvent")
    public ResponseEntity<Void> sendChannelFollowEvent(HttpServletRequest request, Model model,
            @RequestParam(name = "channelName", required = true) String channelName) {
        String userId = getUserId(request);
        ChannelEvent event = new ChannelEvent();
        event.setChannelName(channelName);
        event.setSourceUserId(userId);
        event.setType(ChannelEventType.FOLLOW);
        logger.debug("Publishing ChannelEvent: {}", event);
        applicationContext.publishEvent(event);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/event/sendSubscribeEvent")
    public ResponseEntity<Void> sendChannelSubscribeEvent(HttpServletRequest request, Model model,
            @RequestParam(name = "channelName", required = true) String channelName) {
        String userId = getUserId(request);
        ChannelEvent event = new ChannelEvent();
        event.setChannelName(channelName);
        event.setSourceUserId(userId);
        event.setType(ChannelEventType.SUBSCRIBE);
        logger.debug("Publishing ChannelEvent: {}", event);
        applicationContext.publishEvent(event);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/event/sendGiftEvent")
    public ResponseEntity<Void> sendChannelSubscribeGiftEvent(HttpServletRequest request, Model model,
            @RequestParam(name = "channelName", required = true) String channelName) {
        String userId = getUserId(request);
        ChannelSubGiftEvent event = new ChannelSubGiftEvent();
        event.setChannelName(channelName);
        event.setSourceUserId(userId);
        event.setType(ChannelEventType.GIFT);
        logger.debug("Publishing ChannelSubGiftEvent: {}", event);
        applicationContext.publishEvent(event);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
