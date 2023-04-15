package tv.memoryleakdeath.hex.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import tv.memoryleakdeath.hex.backend.security.HexAuthenticationProvider;
import tv.memoryleakdeath.hex.backend.security.HexUserDetailsService;
import tv.memoryleakdeath.hex.backend.security.HexWebAuthenticationDetailsSource;

@Configuration
@EnableWebSecurity
public class HexSecurity {

    @Autowired
    private HexWebAuthenticationDetailsSource authenticationDetailsSource;

    @Autowired
    private HexUserDetailsService userDetailsService;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:[*]", "https://localhost:[*]"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        HexAuthenticationProvider authProvider = new HexAuthenticationProvider();
        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setUserDetailsService(userDetailsService);
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder(16, 32, 1, 4092, 3);
    }

    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/api/**").cors(cors -> cors.disable()).authorizeHttpRequests(authorize -> authorize.anyRequest().hasRole("API"))
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain authenticatedFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/settings/**", "/dashboard/**").cors(cors -> cors.disable())
                .authorizeHttpRequests(authorize -> authorize.anyRequest().hasRole("USER")).httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    @Order(3)
    public SecurityFilterChain adminFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/admin/**").cors(cors -> cors.disable()).authorizeHttpRequests(authorize -> authorize.anyRequest().hasRole("ADMIN"))
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    @Order(4)
    public SecurityFilterChain allAccessFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/**").cors(cors -> cors.configurationSource(corsConfigurationSource())).authorizeHttpRequests(authorize -> authorize.requestMatchers("/**").permitAll())
                .formLogin(login -> login.authenticationDetailsSource(authenticationDetailsSource).loginPage("/login").defaultSuccessUrl("/").failureUrl("/login?error=true")
                        .loginProcessingUrl("/logincheck"))
                .logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/").deleteCookies("JSESSIONID").invalidateHttpSession(true).clearAuthentication(true));
        return http.build();
    }

}
