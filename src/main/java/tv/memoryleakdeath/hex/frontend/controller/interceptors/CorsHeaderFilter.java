package tv.memoryleakdeath.hex.frontend.controller.interceptors;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CorsHeaderFilter extends OncePerRequestFilter {
    private static String serverName = null;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", getServerName(request));
        response.setHeader("Vary", "Origin");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST");
        response.setHeader("Access-Control-Max-Age", "3600");
        filterChain.doFilter(request, response);
    }

    private String getServerName(HttpServletRequest request) {
        String name;
        if (serverName == null) {
            name = request.getScheme() + "://" + request.getServerName();
            int port = request.getLocalPort();
            if (port != 80 && port != 443) {
                name += ":" + port;
            }
        } else {
            name = serverName;
        }
        return name;
    }
}
