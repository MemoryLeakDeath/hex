package tv.memoryleakdeath.hex.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;

@Configuration
@EnableWebMvc
@ComponentScan("tv.memoryleakdeath.hex.controller")
public class HexWebConfig implements WebMvcConfigurer {

	public class WebMvcConfig implements WebMvcConfigurer {

		@Bean
		public SpringResourceTemplateResolver templateResolver() {
			SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
			templateResolver.setPrefix("/WEB-INF/views/");
			templateResolver.setSuffix(".html");
			templateResolver.setTemplateMode("HTML");
			return templateResolver;
		}

		@Bean
		public SpringTemplateEngine templateEngine() {
			SpringTemplateEngine templateEngine = new SpringTemplateEngine();
			templateEngine.setTemplateResolver(templateResolver());
			return templateEngine;
		}

		@Bean
		public ThymeleafViewResolver viewResolver() {
			ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
			viewResolver.setTemplateEngine(templateEngine());
			viewResolver.setOrder(1);
			viewResolver.setViewNames(new String[] { "*.html" });
			return viewResolver;
		}
	}

}
