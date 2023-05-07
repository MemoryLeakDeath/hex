package tv.memoryleakdeath.hex.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.EncodedResourceResolver;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;

import tv.memoryleakdeath.hex.frontend.controller.interceptors.HexControllerInterceptor;
import tv.memoryleakdeath.hex.frontend.controller.interceptors.ThymeleafLayoutInterceptor;

@Configuration
@EnableWebMvc
@ComponentScan("tv.memoryleakdeath.hex.frontend")
public class HexWebConfig implements WebMvcConfigurer, ApplicationContextAware {
    private ApplicationContext ac;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/js/**").addResourceLocations("/WEB-INF/js/").resourceChain(true).addResolver(new EncodedResourceResolver()).addResolver(new PathResourceResolver());
        registry.addResourceHandler("/css/**").addResourceLocations("/WEB-INF/css/").resourceChain(true).addResolver(new EncodedResourceResolver()).addResolver(new PathResourceResolver());
        registry.addResourceHandler("/images/**").addResourceLocations("/WEB-INF/images/").resourceChain(true).addResolver(new EncodedResourceResolver()).addResolver(new PathResourceResolver());
        registry.addResourceHandler("/favicon.png").addResourceLocations("/WEB-INF/images/favicon.png").resourceChain(true).addResolver(new EncodedResourceResolver())
                .addResolver(new PathResourceResolver());
    }

    @Bean
    public SpringResourceTemplateResolver templateResolver() {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setApplicationContext(ac);
        templateResolver.setPrefix("/WEB-INF/views/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCacheable(false);
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
        return viewResolver;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ac = applicationContext;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HexControllerInterceptor());
        registry.addInterceptor(new ThymeleafLayoutInterceptor());
    }

}
