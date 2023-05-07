package tv.memoryleakdeath.hex.frontend.controller.interceptors;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class HexControllerInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(HexControllerInterceptor.class);
    private static final String[] ALLOWED_VERBS = { "GET", "POST" };

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String method = request.getMethod();
        if (!ArrayUtils.contains(ALLOWED_VERBS, method)) {
            logger.debug("HTTP VERB NOT ALLOWED: {}", method);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
        return true;
    }

}
