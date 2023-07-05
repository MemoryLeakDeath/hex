package tv.memoryleakdeath.hex.fontend.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ContextConfiguration;

import tv.memoryleakdeath.hex.backend.dao.TestDao;
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
    }

    @Autowired
    private TestDao mockTestDao;

    @Test
	public void testTestDatabase() throws Exception {
		when(mockTestDao.testDatabase()).thenReturn(false);
		
		getMockMvc().perform(get("/test"))
		.andExpect(status().isOk())
		.andExpect(view().name("layout/main"))
		.andExpect(model().attribute("testResults", false))
		.andExpect(model().attribute("view", "test"));
	}

    @Test
    public void testHomepage() throws Exception {
        getMockMvc().perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("layout/main"))
                .andExpect(model().attribute(BaseFrontendController.PAGE_TITLE, "Home"))
                .andExpect(model().attribute("view", "index"));
    }
}
