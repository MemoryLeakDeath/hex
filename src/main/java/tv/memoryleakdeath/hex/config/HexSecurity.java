package tv.memoryleakdeath.hex.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.annotation.web.configurers.RememberMeConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import tv.memoryleakdeath.hex.backend.security.HexAuthenticationFailureHandler;
import tv.memoryleakdeath.hex.backend.security.HexAuthenticationProvider;
import tv.memoryleakdeath.hex.backend.security.HexAuthenticationSuccessHandler;
import tv.memoryleakdeath.hex.backend.security.HexRememberMeAuthenticationProvider;
import tv.memoryleakdeath.hex.backend.security.HexUserDetailsService;
import tv.memoryleakdeath.hex.backend.security.HexWebAuthenticationDetailsSource;
import tv.memoryleakdeath.hex.backend.security.RememberMeService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class HexSecurity {

    @Autowired
    private HexWebAuthenticationDetailsSource authenticationDetailsSource;

    @Autowired
    private HexUserDetailsService userDetailsService;

    @Autowired
    private RememberMeService rememberMeService;

    @Autowired
    private HexAuthenticationFailureHandler authFailureHandler;

    @Autowired
    private HexAuthenticationSuccessHandler authSuccessHandler;

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
    public AuthenticationProvider rememberMeProvider() {
        return new HexRememberMeAuthenticationProvider();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity security) throws Exception {
        AuthenticationManagerBuilder builder = security.getSharedObject(AuthenticationManagerBuilder.class);
        return builder.authenticationProvider(authenticationProvider()).authenticationProvider(rememberMeProvider()).build();
    }
    
    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public SecurityContextLogoutHandler logoutHandler() {
        return new SecurityContextLogoutHandler();
    }

    @Bean
    @Order(3)
    public SecurityFilterChain authenticatedFilterChain(HttpSecurity http, AuthenticationManager authManager) throws Exception {
        http.headers(commonHeaders()).cors(cors -> cors.disable())
                .securityMatcher("/settings/**", "/dashboard/**", "/developer/**")
                .authorizeHttpRequests(authorize -> authorize.anyRequest().hasRole("USER"))
                .authenticationManager(authManager)
                .sessionManagement(sessionRegistryConfig())
                .rememberMe(rememberMeConfig())
                .formLogin(formLogin()).logout(formLogout());
        return http.build();
    }

    @Bean
    @Order(4)
    public SecurityFilterChain adminFilterChain(HttpSecurity http, AuthenticationManager authManager) throws Exception {
        http.headers(commonHeaders()).cors(cors -> cors.disable()).securityMatcher("/admin/**")
                .authorizeHttpRequests(authorize -> authorize.anyRequest().hasRole("ADMIN"))
                .authenticationManager(authManager)
                .sessionManagement(sessionRegistryConfig())
                .formLogin(formLogin()).logout(formLogout());
        return http.build();
    }

    @Bean
    @Order(5)
    public SecurityFilterChain allAccessFilterChain(HttpSecurity http, AuthenticationManager authManager) throws Exception {
        http.headers(commonHeaders()).cors(cors -> cors.disable()).securityMatcher("/**")
                .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
                .rememberMe(rememberMeConfig())
                .authenticationManager(authManager)
                .sessionManagement(sessionRegistryConfig())
                .formLogin(formLogin()).logout(formLogout());
        return http.build();
    }

    private Customizer<FormLoginConfigurer<HttpSecurity>> formLogin() {
        return login -> login.authenticationDetailsSource(authenticationDetailsSource).loginPage("/login").successHandler(new HexAuthenticationSuccessHandler())
                .failureHandler(new HexAuthenticationFailureHandler())
                .loginProcessingUrl("/logincheck");
    }

    private Customizer<LogoutConfigurer<HttpSecurity>> formLogout() {
        return logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/").deleteCookies("JSESSIONID", RememberMeService.REMEMBER_ME_PARAM).invalidateHttpSession(true).clearAuthentication(true);
    }

    private Customizer<RememberMeConfigurer<HttpSecurity>> rememberMeConfig() {
        return me -> me.rememberMeServices(rememberMeService);
    }

    private Customizer<HeadersConfigurer<HttpSecurity>> commonHeaders() {
        return headers -> headers.frameOptions(frame -> frame.deny());
    }

    private Customizer<SessionManagementConfigurer<HttpSecurity>> sessionRegistryConfig() {
        return registry -> registry.sessionConcurrency(c -> c.sessionRegistry(sessionRegistry()));
    }
}
