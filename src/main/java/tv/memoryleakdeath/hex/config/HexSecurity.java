package tv.memoryleakdeath.hex.config;

import java.util.Arrays;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class HexSecurity {

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
    @Autowired
    public UserDetailsService userDetailsService(DataSource dataSource) throws Exception {
        UserDetails user = User.withDefaultPasswordEncoder().username("user").password("password").roles("USER").build();
        UserDetails admin = User.withDefaultPasswordEncoder().username("admin").password("password").roles("USER", "ADMIN").build();
        UserDetails api = User.withDefaultPasswordEncoder().username("api").password("password").roles("API").build();
        JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);
        manager.setCreateUserSql("insert into users (username, password, active) values (?,?,?)");
        manager.setUpdateUserSql("update users set password = ?, active = ? where username = ?");
        manager.setChangePasswordSql("update users set password = ? where username = ?");
        manager.setDeleteUserSql("update users set active = false where username = ?");
        manager.setUserExistsSql("select username from users where username = ?");
        manager.setUsersByUsernameQuery("select username, password, active from users where username = ?");
        manager.createUser(user);
        manager.createUser(admin);
        manager.createUser(api);
        return manager;
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
                .formLogin(login -> login.loginPage("/login").defaultSuccessUrl("/").failureUrl("/login?error=true").loginProcessingUrl("/logincheck"))
                .logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/").deleteCookies("JSESSIONID").invalidateHttpSession(true).clearAuthentication(true));
        return http.build();
    }

}
