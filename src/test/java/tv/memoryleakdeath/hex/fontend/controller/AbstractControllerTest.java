package tv.memoryleakdeath.hex.fontend.controller;

import java.util.Arrays;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import net.datafaker.Faker;
import tv.memoryleakdeath.hex.backend.security.HexUserDetailsService;
import tv.memoryleakdeath.hex.common.pojo.HexUser;
import tv.memoryleakdeath.hex.config.HexApplicationConfig;
import tv.memoryleakdeath.hex.config.HexSecurity;
import tv.memoryleakdeath.hex.config.HexWebConfig;
import tv.memoryleakdeath.hex.config.HexWebSocketConfig;
import tv.memoryleakdeath.hex.test.config.MockDBConfig;

@ExtendWith(MockitoExtension.class)
@SpringJUnitWebConfig({ HexWebConfig.class, MockDBConfig.class, HexApplicationConfig.class, HexSecurity.class,
        HexWebSocketConfig.class })
@Tag("unit-test")
public abstract class AbstractControllerTest {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(SecurityMockMvcConfigurers.springSecurity()).build();
    }

    public MockMvc getMockMvc() {
        return mockMvc;
    }

    @Configuration
    abstract static class Config {

        @Bean
        @Primary
        public UserDetailsService getMockUserDetailsService() {
            return Mockito.mock(HexUserDetailsService.class);
        }
    }

    public HexUser dummyNormalUser() {
        Faker faker = new Faker();
        HexUser normalUser = new HexUser("user", "password12", true, true, true, true, Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
        normalUser.setDisplayName(faker.greekPhilosopher().name());
        normalUser.setId(UUID.randomUUID().toString());
        String emailAddress = faker.appliance().brand();
        normalUser.setGravatarId(DigestUtils.md5Hex(emailAddress));
        return normalUser;
    }

}
