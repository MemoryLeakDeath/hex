package tv.memoryleakdeath.hex.frontend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import tv.memoryleakdeath.hex.backend.email.EmailService;

@Controller
@RequestMapping("/email")
public class TestEmailController extends BaseFrontendController {
    private static final Logger logger = LoggerFactory.getLogger(TestEmailController.class);

    @Autowired
    private EmailService emailService;

    @GetMapping("/test")
    public String testEmail(HttpServletRequest request, Model model) {
        addCommonModelAttributes(model);
        String to = "testuser@hex-example.com";
        try {
            boolean success = emailService.sendTestEmail(to);
            if (success) {
                addSuccessMessage(model, "text.success.ok");
            } else {
                logger.error("Email service returned an error!");
                addErrorMessage(model, "text.error.systemerror");
            }
        } catch (Exception e) {
            logger.error("Unable to send test email!", e);
            addErrorMessage(model, "text.error.systemerror");
        }
        return "test-email";
    }
}
