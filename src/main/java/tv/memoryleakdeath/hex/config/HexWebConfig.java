package tv.memoryleakdeath.hex.config;

import java.util.Locale;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.resource.EncodedResourceResolver;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;

import tv.memoryleakdeath.hex.frontend.controller.interceptors.HexControllerInterceptor;
import tv.memoryleakdeath.hex.frontend.controller.interceptors.ThymeleafLayoutInterceptor;

@Configuration
@EnableWebMvc
@ComponentScan("tv.memoryleakdeath.hex.frontend")
public class HexWebConfig implements WebMvcConfigurer {

    @Autowired
    private ApplicationContext ac;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/js/**").addResourceLocations("/WEB-INF/js/").resourceChain(true).addResolver(new EncodedResourceResolver()).addResolver(new PathResourceResolver());
        registry.addResourceHandler("/css/**").addResourceLocations("/WEB-INF/css/").resourceChain(true).addResolver(new EncodedResourceResolver()).addResolver(new PathResourceResolver());
        registry.addResourceHandler("/webfonts/**").addResourceLocations("/WEB-INF/webfonts/").resourceChain(true).addResolver(new EncodedResourceResolver()).addResolver(new PathResourceResolver());
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
        templateEngine.addDialect(new SpringSecurityDialect());
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
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HexControllerInterceptor());
        registry.addInterceptor(new ThymeleafLayoutInterceptor());
        registry.addInterceptor(localeChangeInterceptor());
    }

    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("/WEB-INF/messages/messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setDefaultLocale(Locale.US);
        messageSource.setCacheSeconds(2);
        return messageSource;
    }

    @Bean
    public SessionLocaleResolver localeResolver() {
        SessionLocaleResolver resolver = new SessionLocaleResolver();
        resolver.setDefaultLocale(Locale.US);
        resolver.setDefaultTimeZone(TimeZone.getTimeZone("America/New_York"));
        return resolver;
    }

    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("language");
        return interceptor;
    }

}
