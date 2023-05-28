package tv.memoryleakdeath.hex.frontend.controller;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.ui.Model;
import org.springframework.web.servlet.LocaleResolver;

import jakarta.servlet.http.HttpServletRequest;

public abstract class BaseFrontendController {
    public static final String PAGE_TITLE = "pageTitle";

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private LocaleResolver localeResolver;

    protected Locale getLocale(HttpServletRequest request) {
        return localeResolver.resolveLocale(request);
    }

    protected String getMessage(HttpServletRequest request, String msg) {
        return messageSource.getMessage(msg, null, msg, getLocale(request));
    }

    protected void setPageTitle(HttpServletRequest request, Model model, String titleMsg) {
        model.addAttribute(PAGE_TITLE, getMessage(request, titleMsg));
    }
}
