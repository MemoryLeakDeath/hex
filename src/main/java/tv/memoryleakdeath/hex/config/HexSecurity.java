package tv.memoryleakdeath.hex.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

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

//    @Bean
//    public CorsFilter corsFilter() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowCredentials(true);
//        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:[*]", "https://localhost:[*]"));
//        configuration.setAllowedMethods(Arrays.asList("GET", "POST"));
//        configuration.setAllowedHeaders(Arrays.asList(CorsConfiguration.ALL));
//        configuration.setExposedHeaders(Arrays.asList(CorsConfiguration.ALL));
//
//        CorsConfiguration apiConfiguration = new CorsConfiguration();
//        configuration.setAllowCredentials(true);
//        configuration.setAllowedOriginPatterns(Arrays.asList(CorsConfiguration.ALL));
//        configuration.setAllowedMethods(Arrays.asList("GET", "POST"));
//        configuration.setAllowedHeaders(Arrays.asList(CorsConfiguration.ALL));
//        configuration.setExposedHeaders(Arrays.asList(CorsConfiguration.ALL));
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        Map<String, CorsConfiguration> configs = new LinkedHashMap<>();
//        configs.put("/api/**", apiConfiguration);
//        configs.put("/**", configuration);
//
//        source.setCorsConfigurations(configs);
//        return new CorsFilter(source);
//    }

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
        http.cors(Customizer.withDefaults()).securityMatcher("/api/**").authorizeHttpRequests(authorize -> authorize.anyRequest().hasRole("API")).httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain authenticatedFilterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults()).securityMatcher("/settings/**", "/dashboard/**").authorizeHttpRequests(authorize -> authorize.anyRequest().hasRole("USER"))
                .formLogin(formLogin()).logout(formLogout());
        return http.build();
    }

    @Bean
    @Order(3)
    public SecurityFilterChain adminFilterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults()).securityMatcher("/admin/**").authorizeHttpRequests(authorize -> authorize.anyRequest().hasRole("ADMIN"))
                .formLogin(formLogin()).logout(formLogout());
        return http.build();
    }

    @Bean
    @Order(4)
    public SecurityFilterChain allAccessFilterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults()).securityMatcher("/**").authorizeHttpRequests(authorize -> authorize.requestMatchers("/**").permitAll())
                .formLogin(formLogin()).logout(formLogout());
        return http.build();
    }

    private Customizer<FormLoginConfigurer<HttpSecurity>> formLogin() {
        return login -> login.authenticationDetailsSource(authenticationDetailsSource).loginPage("/login").defaultSuccessUrl("/").failureUrl("/login?error=true")
                .loginProcessingUrl("/logincheck");
    }

    private Customizer<LogoutConfigurer<HttpSecurity>> formLogout() {
        return logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/").deleteCookies("JSESSIONID").invalidateHttpSession(true).clearAuthentication(true);
    }

}
