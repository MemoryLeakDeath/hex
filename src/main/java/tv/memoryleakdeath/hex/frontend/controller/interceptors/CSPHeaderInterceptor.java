package tv.memoryleakdeath.hex.frontend.controller.interceptors;

import java.security.SecureRandom;

import org.apache.commons.codec.binary.Base64;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tv.memoryleakdeath.hex.frontend.controller.BaseFrontendController;

public class CSPHeaderInterceptor implements HandlerInterceptor {
    private static final String CSP_POLICY = "default-src 'self' data:; script-src 'self' 'nonce-%s'; img-src 'self' data: www.gravatar.com;";
    private static final int NONCE_SIZE = 128;
    private static final String HTMX_CONFIG = "{\"inlineScriptNonce\":\"%s\", \"includeIndicatorStyles\":false}";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler.getClass().isAssignableFrom(ResourceHttpRequestHandler.class)) {
            return true;
        }
        String nonce = "";
        if (request.getAttribute(BaseFrontendController.NONCE) == null) {
            nonce = generateNonce();
        } else {
            nonce = (String) request.getAttribute(BaseFrontendController.NONCE);
        }
        request.setAttribute(BaseFrontendController.NONCE, nonce);
        request.setAttribute(BaseFrontendController.HTMX_CONFIG, HTMX_CONFIG.formatted(nonce));
        return true;
    }

    private String generateNonce() {
        byte[] nonceBytes = new byte[NONCE_SIZE];
        new SecureRandom().nextBytes(nonceBytes);
        return Base64.encodeBase64URLSafeString(nonceBytes);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (handler.getClass().isAssignableFrom(ResourceHttpRequestHandler.class)) {
            return;
        }
        String nonce = (String) request.getAttribute(BaseFrontendController.NONCE);
        if (nonce == null) {
            nonce = "";
        }
        response.setHeader("Content-Security-Policy", CSP_POLICY.formatted(nonce));
        if (modelAndView != null && modelAndView.getModel() != null) {
            modelAndView.getModel().put(BaseFrontendController.NONCE, nonce);
        }
    }

}
