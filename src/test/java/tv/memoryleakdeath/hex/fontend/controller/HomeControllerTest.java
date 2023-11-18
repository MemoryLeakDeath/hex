package tv.memoryleakdeath.hex.fontend.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ContextConfiguration;

import tv.memoryleakdeath.hex.backend.dao.TestDao;
import tv.memoryleakdeath.hex.backend.dao.channel.ChannelsDao;
import tv.memoryleakdeath.hex.common.pojo.HexUser;
import tv.memoryleakdeath.hex.frontend.controller.BaseFrontendController;

@ContextConfiguration(classes = { HomeControllerTest.Config.class })
public class HomeControllerTest extends AbstractControllerTest {

    @Configuration
    static class Config extends AbstractControllerTest.Config {

        @Bean
        @Primary
        public TestDao getTestDao() {
            return Mockito.mock(TestDao.class);
        }

        @Bean
        @Primary
        public ChannelsDao getChannelsDao() {
            return Mockito.mock(ChannelsDao.class);
        }
    }

    @Autowired
    private TestDao mockTestDao;

    @Autowired
    private ChannelsDao mockChannelsDao;

    @Autowired
    private UserDetailsService mockUserDetailsService;

    @Test
    public void testTestDatabase() throws Exception {
        when(mockTestDao.testDatabase()).thenReturn(false);
        
        getMockMvc().perform(get("/test").with(csrf()))
        .andExpect(status().isOk())
        .andExpect(view().name("layout/main"))
        .andExpect(model().attribute("testResults", false))
        .andExpect(model().attribute("view", "test"));
    }

    @Test
    @WithAnonymousUser
    public void testAnonymousHomepage() throws Exception {
        getMockMvc().perform(get("/").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("layout/main"))
                .andExpect(model().attribute(BaseFrontendController.PAGE_TITLE, "Home"))
                .andExpect(model().attribute("view", "index"))
                .andExpect(content().string(containsString("topNavRegistrationButton")))
                .andExpect(content().string(containsString("topNavLoginButton")));
    }

    @Test
    public void testUserHomepage() throws Exception {
        HexUser user = dummyNormalUser();
        when(mockChannelsDao.hasChannel(any())).thenReturn(false);
        getMockMvc().perform(get("/").with(csrf()).with(user(user)))
                .andExpect(status().isOk())
                .andExpect(view().name("layout/main"))
                .andExpect(model().attribute(BaseFrontendController.PAGE_TITLE, "Home"))
                .andExpect(model().attribute("view", "index"))
                .andExpect(content().string(containsString("userAvatar")))
                .andExpect(content().string(containsString("userDisplayName")))
                .andExpect(content().string(containsString(user.getDisplayName())));
    }
}
