package tv.memoryleakdeath.hex.frontend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tv.memoryleakdeath.hex.backend.dao.channel.ChannelsDao;
import tv.memoryleakdeath.hex.backend.follower.FollowerService;

@Controller
@RequestMapping("/channel")
public class ChannelController extends BaseFrontendController {
    private static final Logger logger = LoggerFactory.getLogger(ChannelController.class);

    @Autowired
    private ChannelsDao channelsDao;

    @Autowired
    private FollowerService followerService;

    @GetMapping("/{channelName}")
    public String view(HttpServletRequest request, Model model,
            @PathVariable(name = "channelName", required = true) String channelName) {
        setPageTitle(request, model, "title.channel", new String[] { channelName });
        setLayout(model, "layout/main");
        model.addAttribute("channelPageName", channelName);
        try {
            boolean channelExists = channelsDao.channelNameExists(channelName);
            if (!channelExists) {
                return "redirect:/";
            }
            String userId = getUserId(request);
            boolean isFollowing = followerService.isCurrentlyFollowing(userId, channelName);
            if (isFollowing) {
                model.addAttribute("showUnFollowButton", true);
            } else {
                model.addAttribute("showFollowButton", true);
            }
        } catch (Exception e) {
            logger.error("Unable to display channel page!", e);
            addErrorMessage(request, "text.error.systemerror");
            return "redirect:/";
        }
        return "channel/channel";
    }

    @PostMapping("/{channelName}/follow")
    public String followChannel(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable(name = "channelName", required = true) String channelName) {
        setLayout(model, "layout/minimal");
        try {
            model.addAttribute("channelPageName", channelName);
            boolean channelExists = channelsDao.channelNameExists(channelName);
            if (!channelExists) {
                logger.error("Cannot follow channel: {} channel does not exist!", channelName);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                addErrorMessage(request, "text.error.systemerror");
            }
            String currentUserId = getUserId(request);
            boolean result = followerService.startFollowingChannel(currentUserId, channelName);
            if (!result) {
                logger.error("Unable to add user: {} as follower to channel: {}", currentUserId, channelName);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                model.addAttribute("showFollowButton", true);
                addErrorMessage(request, "text.error.systemerror");
            } else {
                model.addAttribute("showUnFollowButton", true);
            }
        } catch (Exception e) {
            logger.error("Unable to follow channel: " + channelName, e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            addErrorMessage(request, "text.error.systemerror");
        }
        return "fragment/follow-button";
    }

    @PostMapping("/{channelName}/unfollow")
    public String unfollowChannel(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable(name = "channelName", required = true) String channelName) {
        setLayout(model, "layout/minimal");
        try {
            model.addAttribute("channelPageName", channelName);
            boolean channelExists = channelsDao.channelNameExists(channelName);
            if (!channelExists) {
                logger.error("Cannot un-follow channel: {} channel does not exist!", channelName);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                addErrorMessage(request, "text.error.systemerror");
            }
            String currentUserId = getUserId(request);
            boolean result = followerService.stopFollowingChannel(currentUserId, channelName);
            if (!result) {
                logger.error("Unable to remove user: {} as follower to channel: {}", currentUserId, channelName);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                model.addAttribute("showUnFollowButton", true);
                addErrorMessage(request, "text.error.systemerror");
            } else {
                model.addAttribute("showFollowButton", true);
            }
        } catch (Exception e) {
            logger.error("Unable to un-follow channel: " + channelName, e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            addErrorMessage(request, "text.error.systemerror");
        }
        return "fragment/follow-button";
    }
}
