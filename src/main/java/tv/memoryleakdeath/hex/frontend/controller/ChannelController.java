package tv.memoryleakdeath.hex.frontend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/channel")
public class ChannelController extends BaseFrontendController {
    private static final Logger logger = LoggerFactory.getLogger(ChannelController.class);

    @GetMapping("/{channelName}")
    public String view(HttpServletRequest request, Model model,
            @PathVariable(name = "channelName", required = true) String channelName) {
        setPageTitle(request, model, "title.channel", new String[] { channelName });
        setLayout(model, "layout/main");
        model.addAttribute("channelPageName", channelName);
        return "channel/channel";
    }
}
