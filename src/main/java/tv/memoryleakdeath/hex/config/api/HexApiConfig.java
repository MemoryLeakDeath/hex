package tv.memoryleakdeath.hex.config.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@ComponentScan("tv.memoryleakdeath.hex.api")
public class HexApiConfig implements WebMvcConfigurer {
    public static final String[] API_AUTHORITIES = { "SCOPE_BASICREAD", "SCOPE_BASICWRITE", "SCOPE_CHAT",
            "SCOPE_CHANNELREAD", "SCOPE_CHANNELWRITE", "SCOPE_STREAMCHAT", "SCOPE_ACCOUNTFULL", "SCOPE_INTERACTIVE" };

    @Bean
    @Order(2)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/api/**")
                .authorizeHttpRequests(auth -> auth.anyRequest().hasAnyAuthority(API_AUTHORITIES))
                .oauth2ResourceServer((resource) -> resource.jwt(Customizer.withDefaults()));
        return http.build();
    }

}
