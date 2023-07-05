package tv.memoryleakdeath.hex.fontend.controller;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ContextConfiguration;

import tv.memoryleakdeath.hex.backend.dao.security.AuthenticationDao;

@ContextConfiguration(classes = { LoginControllerTest.Config.class })
public class LoginControllerTest extends AbstractControllerTest {

    @Configuration
    static class Config extends AbstractControllerTest.Config {

        @Bean
        @Primary
        public AuthenticationDao getAuthenticationDao() {
            return Mockito.mock(AuthenticationDao.class);
        }
    }

    @Autowired
    private AuthenticationDao mockAuthDao;
}
