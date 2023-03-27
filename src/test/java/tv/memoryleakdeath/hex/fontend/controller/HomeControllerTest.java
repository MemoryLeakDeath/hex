package tv.memoryleakdeath.hex.fontend.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import tv.memoryleakdeath.hex.backend.dao.TestDao;
import tv.memoryleakdeath.hex.config.HexApplicationConfig;
import tv.memoryleakdeath.hex.config.HexWebConfig;
import tv.memoryleakdeath.hex.test.config.TestDBConfig;

@ExtendWith(MockitoExtension.class)
@SpringJUnitWebConfig({ HexWebConfig.class, TestDBConfig.class, HexApplicationConfig.class,
		HomeControllerTest.Config.class })
public class HomeControllerTest {

	@Configuration
	static class Config {

		@Bean
		@Primary
		public TestDao getTestDao() {
			return Mockito.mock(TestDao.class);
		}
	}

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private TestDao mockTestDao;

	@BeforeEach
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
	}

	@Test
	public void testTestDatabase() throws Exception {
		when(mockTestDao.testDatabase()).thenReturn(false);
		
		mockMvc.perform(get("/test")).andExpect(status().isOk()).andExpect(view().name("test")).andExpect(model().attribute("testResults", false));
	}
}
