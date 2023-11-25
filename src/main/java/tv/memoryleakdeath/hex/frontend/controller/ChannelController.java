package tv.memoryleakdeath.hex.frontend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import tv.memoryleakdeath.hex.backend.dao.channel.ChannelsDao;

@Controller
@RequestMapping("/channel")
public class ChannelController extends BaseFrontendController {
    private static final Logger logger = LoggerFactory.getLogger(ChannelController.class);

    @Autowired
    private ChannelsDao channelsDao;

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
        } catch (Exception e) {
            logger.error("Unable to display channel page!", e);
            addErrorMessage(request, "text.error.systemerror");
            return "redirect:/";
        }
        return "channel/channel";
    }
}
