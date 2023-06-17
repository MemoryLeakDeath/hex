package tv.memoryleakdeath.hex.backend.email;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMessage.RecipientType;
import tv.memoryleakdeath.hex.backend.dao.email.EmailTemplateDao;
import tv.memoryleakdeath.hex.common.pojo.EmailTemplate;
import tv.memoryleakdeath.hex.common.pojo.EmailTemplateType;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private EmailTemplateDao templateDao;

    @Autowired
    private Session emailSession;

    @Autowired
    @Qualifier("defaultEmailFrom")
    private String emailFrom;

    public boolean sendTestEmail(String recipient) {
        EmailTemplate template = templateDao.getTemplateByLocaleOrDefault(EmailTemplateType.test, Locale.US);
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("testVar", "Hello World!");
        templateVariables.put("testSubject", "This is a test email from Hex");
        String[] parsedTemplateParts = parseTemplateSubjectAndBody(template, "testTemplate", templateVariables);
        return sendMessage(recipient, parsedTemplateParts[0], parsedTemplateParts[1]);
    }

    private String[] parseTemplateSubjectAndBody(EmailTemplate template, String logTemplateName, Map<String, Object> variables) {
        Velocity.init();
        VelocityContext context = new VelocityContext(variables);
        StringWriter body = new StringWriter();
        Velocity.evaluate(context, body, logTemplateName, template.getBody());
        StringWriter subject = new StringWriter();
        Velocity.evaluate(context, subject, logTemplateName, template.getSubject());
        return new String[] { subject.toString(), body.toString() };
    }

    private boolean sendMessage(String recipient, String subject, String body) {
        Message message = new MimeMessage(emailSession);
        try {
            message.setFrom(new InternetAddress(emailFrom));
            message.setRecipient(RecipientType.TO, new InternetAddress(recipient));
            message.setSubject(subject);
            message.setText(body);
            Transport.send(message);
        } catch (MessagingException e) {
            logger.error("Unable to create email message!", e);
            return false;
        }
        return true;
    }
}
