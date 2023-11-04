package tv.memoryleakdeath.hex.frontend.controller.api.interceptors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtTokenBlacklistCheckerFilter extends SecurityContextHolderAwareRequestFilter {
    private static List<String> tokenBlacklist = new ArrayList<>();

    public static List<String> getTokenBlacklist() {
        return tokenBlacklist;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        boolean okToContinue = true;
        if (!((req instanceof HttpServletRequest httpRequest) && (res instanceof HttpServletResponse httpResponse))) {
            throw new ServletException("JwtTokenBlacklistCheckerFilter only supports HTTP requests");
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken) {
            String tokenValue = ((Jwt) auth.getPrincipal()).getTokenValue();
            if (tokenBlacklist.contains(tokenValue)) {
                httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                okToContinue = false;
            }
        }
        if (okToContinue) {
            chain.doFilter(httpRequest, httpResponse);
        }
    }

}
