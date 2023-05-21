package tv.memoryleakdeath.hex.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan("tv.memoryleakdeath.hex.backend")
@PropertySource("classpath:hex.properties")
public class HexApplicationConfig {

    @Value("${totpKey}")
    private String totpKey;

    @Value("${rememberMeKey}")
    private String rememberMeKey;

    @Bean
    public String totpKey() {
        return totpKey;
    }

    @Bean
    public String rememberMeKey() {
        return rememberMeKey;
    }
}
