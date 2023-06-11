package tv.memoryleakdeath.hex.frontend.controller;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMessage.RecipientType;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/email")
public class TestEmailController extends BaseFrontendController {
    private static final Logger logger = LoggerFactory.getLogger(TestEmailController.class);

    @GetMapping("/test")
    public String testEmail(HttpServletRequest request, Model model) {
        addCommonModelAttributes(model);
        String to = "testuser@hex-example.com";
        String from = "hex-internal@hex-example.com";
        String subject = "test email";

        Properties mailProps = new Properties();
        mailProps.setProperty("mail.host", "localhost");
        mailProps.setProperty("mail.smtp.port", "10025");
        mailProps.setProperty("mail.smtp.starttls.enable", "true");
        Session mailSession = Session.getDefaultInstance(mailProps);

        Message message = new MimeMessage(mailSession);
        try {
            message.setFrom(new InternetAddress(from));
            message.setRecipient(RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            message.setText("This is a test of the email system");
            Transport.send(message);
        } catch (MessagingException e) {
            logger.error("Unable to create email message!", e);
            addErrorMessage(model, "text.error.systemerror");
        }
        addSuccessMessage(model, "text.success.ok");
        return "test-email";
    }
}
