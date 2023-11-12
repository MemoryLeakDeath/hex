package tv.memoryleakdeath.hex.frontend.controller.dashboard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import tv.memoryleakdeath.hex.frontend.controller.BaseFrontendController;

@Controller
@RequestMapping("/dashboard")
public class StreamerDashboardController extends BaseFrontendController {
    private static final Logger logger = LoggerFactory.getLogger(StreamerDashboardController.class);

    @GetMapping("/")
    public String view(HttpServletRequest request, Model model) {
        setPageTitle(request, model, "title.dashboard");
        setLayout(model, "layout/dashboard");
        return "dashboard/landingpage";
    }
}
