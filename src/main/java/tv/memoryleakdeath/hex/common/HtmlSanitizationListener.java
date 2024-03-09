package tv.memoryleakdeath.hex.common;

import org.owasp.html.HtmlChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HtmlSanitizationListener implements HtmlChangeListener<Object> {
    private static final Logger logger = LoggerFactory.getLogger(HtmlSanitizationListener.class);
    private String attributeName;
    private boolean hasSanitizied = false;

    public HtmlSanitizationListener(String attributeName) {
        this.attributeName = attributeName;
    }

    public boolean hasSanitizied() {
        return this.hasSanitizied;
    }

    @Override
    public void discardedTag(Object context, String elementName) {
        logger.warn("[HTML SANITIZER] Blocked element: {} from request attribute: {}", elementName, attributeName);
        this.hasSanitizied = true;
    }

    @Override
    public void discardedAttributes(Object context, String tagName, String... attributeNames) {
        logger.warn("[HTML SANITIZER] Blocked tag: {} from request attribute: {}", tagName, attributeName);
        this.hasSanitizied = true;
    }
}

