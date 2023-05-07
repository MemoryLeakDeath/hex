package tv.memoryleakdeath.hex.frontend.controller.interceptors;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CorsHeaderFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String serverName = request.getScheme() + "://" + request.getServerName();
        int port = request.getLocalPort();
        if (port != 80 && port != 443) {
            serverName += ":" + port;
        }
        response.setHeader("Access-Control-Allow-Origin", serverName);
        response.setHeader("Vary", "Origin");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST");
        response.setHeader("Access-Control-Max-Age", "3600");
        filterChain.doFilter(request, response);
    }

}
