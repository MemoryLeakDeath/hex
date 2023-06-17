package tv.memoryleakdeath.hex.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import jakarta.mail.Session;

@Configuration
@ComponentScan("tv.memoryleakdeath.hex.backend")
@PropertySource("classpath:hex.properties")
public class HexApplicationConfig {

    @Value("${totpKey}")
    private String totpKey;

    @Value("${rememberMeKey}")
    private String rememberMeKey;

    @Value("${emailHost}")
    private String emailHost;

    @Value("${emailPort}")
    private String emailPort;

    @Value("${emailTLS}")
    private String emailTLS;

    @Value("${emailFrom}")
    private String emailFrom;

    @Bean
    public String totpKey() {
        return totpKey;
    }

    @Bean
    public String rememberMeKey() {
        return rememberMeKey;
    }

    @Bean
    public String defaultEmailFrom() {
        return emailFrom;
    }

    @Bean
    public Session emailSession() {
        Properties mailProps = new Properties();
        mailProps.setProperty("mail.host", emailHost);
        mailProps.setProperty("mail.smtp.port", emailPort);
        mailProps.setProperty("mail.smtp.starttls.enable", emailTLS);
        return Session.getDefaultInstance(mailProps);
    }

}
