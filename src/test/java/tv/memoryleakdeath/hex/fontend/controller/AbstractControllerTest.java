package tv.memoryleakdeath.hex.fontend.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import tv.memoryleakdeath.hex.config.HexApplicationConfig;
import tv.memoryleakdeath.hex.config.HexSecurity;
import tv.memoryleakdeath.hex.config.HexWebConfig;
import tv.memoryleakdeath.hex.test.config.MockDBConfig;

@ExtendWith(MockitoExtension.class)
@SpringJUnitWebConfig({ HexWebConfig.class, MockDBConfig.class, HexApplicationConfig.class, HexSecurity.class, AbstractControllerTest.Config.class })
public abstract class AbstractControllerTest {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    public MockMvc getMockMvc() {
        return mockMvc;
    }

    @Configuration
    static abstract class Config {

    }

}
