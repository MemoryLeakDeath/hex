package tv.memoryleakdeath.hex.test.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:hex-test.properties")
public class TestCommonConfig {
    @Autowired
    private Environment env;

    @Bean
    public String uploadsBaseDir() {
        return env.getProperty("uploadsdir");
    }
}
