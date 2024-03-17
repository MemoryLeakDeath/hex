package tv.memoryleakdeath.hex.frontend.controller.interceptors;

import java.util.ArrayList;
import java.util.List;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import tv.memoryleakdeath.hex.common.HtmlSanitizationListener;

public class HtmlSanitizerRequestWrapper extends HttpServletRequestWrapper {
    private static final Logger logger = LoggerFactory.getLogger(HtmlSanitizerRequestWrapper.class);
    private static PolicyFactory rejectPolicyFactory = new HtmlPolicyBuilder().toFactory();

    public HtmlSanitizerRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        List<String> newValues = new ArrayList<>();
        for (String value : values) {
            newValues.add(sanitizeValue(name, value));
        }
        return newValues.toArray(String[]::new);
    }

    private String sanitizeValue(final String attributeName, String rawValue) {
        HtmlSanitizationListener listener = new HtmlSanitizationListener(attributeName);
        String sanitizedString = rejectPolicyFactory.sanitize(rawValue, listener, null);
        if (listener.hasSanitizied()) {
            logger.debug("[HTML SANITIZER] Returning sanitized input!");
            return sanitizedString;
        }
        return rawValue;
    }

}
