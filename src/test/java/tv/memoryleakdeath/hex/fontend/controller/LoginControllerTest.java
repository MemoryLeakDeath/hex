package tv.memoryleakdeath.hex.fontend.controller;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import tv.memoryleakdeath.hex.backend.dao.security.AuthenticationDao;

public class LoginControllerTest extends AbstractControllerTest {

    @Configuration
    static class Config {

        @Bean
        @Primary
        public AuthenticationDao getAuthenticationDao() {
            return Mockito.mock(AuthenticationDao.class);
        }
    }

    @Autowired
    private AuthenticationDao mockAuthDao;
}
