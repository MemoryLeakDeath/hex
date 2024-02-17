package tv.memoryleakdeath.hex.config;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;
import tv.memoryleakdeath.hex.frontend.controller.api.interceptors.JwtTokenBlacklistCheckerFilter;
import tv.memoryleakdeath.hex.frontend.controller.interceptors.CorsHeaderFilter;

public class HexInit implements WebApplicationInitializer {
    private static final long MAX_UPLOAD_FILE_SIZE = 10L * 1024 * 1024; // 10MB
    private static final long MAX_UPLOAD_REQUEST_SIZE = 5L * MAX_UPLOAD_FILE_SIZE; // 50 MB
    private static final int FILE_SIZE_THRESHOLD = 50 * 1024; // 50 KB
    private static final String TEMP_FOLDER = System.getProperty("java.io.tmpdir");

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
        rootContext.scan("tv.memoryleakdeath.hex.config");

        // Manage the lifecycle of the root application context
        servletContext.addListener(new ContextLoaderListener(rootContext));

        // Add spring security filter chain to servlet context
        servletContext.addFilter("securityFilter", new DelegatingFilterProxy("springSecurityFilterChain")).addMappingForUrlPatterns(null, false, "/*");
        servletContext.addFilter("corsFilter", new CorsHeaderFilter()).addMappingForUrlPatterns(null, true, "/*");
        servletContext.addFilter("jwtTokenBlacklistFilter", new JwtTokenBlacklistCheckerFilter())
                .addMappingForUrlPatterns(null, true, "/api/*");
        servletContext.addFilter("characterEncodingFilter", new CharacterEncodingFilter("UTF-8", true))
                .addMappingForUrlPatterns(null, true, "/*");
        // servletContext.addFilter("cspFilter", new
        // CSPHeaderFilter()).addMappingForUrlPatterns(null, true, "/*");

        // Register and map the dispatcher servlet
        ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcher", new DispatcherServlet(rootContext));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");
        dispatcher.setMultipartConfig(
                new MultipartConfigElement(TEMP_FOLDER, MAX_UPLOAD_FILE_SIZE,
                MAX_UPLOAD_REQUEST_SIZE, FILE_SIZE_THRESHOLD));
    }

}
