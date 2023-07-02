package tv.memoryleakdeath.hex.frontend.controller.interceptors;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CSPHeaderFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        response.setHeader("Content-Security-Policy", "default-src 'self' data:; script-src 'self' 'nonce-browser-sync'; img-src 'self' data: www.gravatar.com;");
        filterChain.doFilter(request, response);
    }

}
