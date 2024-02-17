package tv.memoryleakdeath.hex.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import jakarta.mail.Session;

@Configuration
@ComponentScan("tv.memoryleakdeath.hex.backend")
@PropertySource("classpath:hex.properties")
public class HexApplicationConfig {

    @Autowired
    private Environment env;

    @Bean
    public String totpKey() {
        return env.getProperty("totpKey");
    }

    @Bean
    public String rememberMeKey() {
        return env.getProperty("rememberMeKey");
    }

    @Bean
    public String defaultEmailFrom() {
        return env.getProperty("emailFrom");
    }

    @Bean
    public Session emailSession() {
        Properties mailProps = new Properties();
        mailProps.setProperty("mail.host", env.getProperty("emailHost", "SETME"));
        mailProps.setProperty("mail.smtp.port", env.getProperty("emailPort", "SETME"));
        mailProps.setProperty("mail.smtp.starttls.enable", env.getProperty("emailTLS", "SETME"));
        return Session.getDefaultInstance(mailProps);
    }

    @Bean
    public String uploadsBaseDir() {
        return env.getProperty("uploadsdir");
    }

}
