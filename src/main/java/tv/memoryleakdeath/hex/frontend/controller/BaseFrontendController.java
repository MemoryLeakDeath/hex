package tv.memoryleakdeath.hex.frontend.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.LocaleResolver;

import jakarta.servlet.http.HttpServletRequest;
import tv.memoryleakdeath.hex.common.pojo.HexUser;
import tv.memoryleakdeath.hex.frontend.controller.interceptors.ThymeleafLayoutInterceptor;

public abstract class BaseFrontendController {
    public static final String PAGE_TITLE = "pageTitle";
    public static final String ERROR_MESSAGES = "pageErrors";
    public static final String INFO_MESSAGES = "pageInfos";
    public static final String WARNING_MESSAGES = "pageWarnings";
    public static final String SUCCESS_MESSAGES = "pageSuccesses";
    public static final String IMAGE_CAPTCHA = "imageCaptcha";
    public static final String AUDIO_CAPTCHA = "audioCaptcha";
    public static final String CSS_SCRIPTS = "pageCSSScripts";
    public static final String JS_SCRIPTS = "pageJSScripts";
    public static final String NONCE = "nonce";

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

    protected void setLayout(Model model, String layoutRelativePath) {
        model.addAttribute(ThymeleafLayoutInterceptor.LAYOUT_NAME_VARIABLE, layoutRelativePath);
    }

    @SuppressWarnings("unchecked")
    protected void addPageCSS(Model model, String relativePath) {
        List<String> cssList = (List<String>) model.getAttribute(CSS_SCRIPTS);
        if (cssList == null) {
            cssList = new ArrayList<>();
            model.addAttribute(CSS_SCRIPTS, cssList);
        }
        cssList.add(relativePath);
    }

    @SuppressWarnings("unchecked")
    protected void addPageJS(Model model, String relativePath) {
        List<String> jsList = (List<String>) model.getAttribute(JS_SCRIPTS);
        if (jsList == null) {
            jsList = new ArrayList<>();
            model.addAttribute(JS_SCRIPTS, jsList);
        }
        jsList.add(relativePath);
    }

    protected String getUserId(HttpServletRequest request) {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) request.getUserPrincipal();
        if (token != null && token.getPrincipal() != null) {
            return ((HexUser) token.getPrincipal()).getId();
        }
        return null;
    }

    protected String getDisplayName(HttpServletRequest request) {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) request.getUserPrincipal();
        if (token != null && token.getPrincipal() != null) {
            return ((HexUser) token.getPrincipal()).getDisplayName();
        }
        return null;
    }

    protected HexUser getUser(HttpServletRequest request) {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) request.getUserPrincipal();
        if (token != null && token.getPrincipal() != null) {
            return (HexUser) token.getPrincipal();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private void addMessage(HttpServletRequest request, String attribute, String key) {
        List<String> messages = (List<String>) request.getSession().getAttribute(attribute);
        if (messages == null) {
            messages = new ArrayList<>();
            request.getSession().setAttribute(attribute, messages);
        }
        messages.add(key);
    }

    protected void addErrorMessage(HttpServletRequest request, String errorKey) {
        addMessage(request, ERROR_MESSAGES, errorKey);
    }

    protected void addSuccessMessage(HttpServletRequest request, String errorKey) {
        addMessage(request, SUCCESS_MESSAGES, errorKey);
    }

    protected void addWarningMessage(HttpServletRequest request, String errorKey) {
        addMessage(request, WARNING_MESSAGES, errorKey);
    }

    protected void addInfoMessage(HttpServletRequest request, String errorKey) {
        addMessage(request, INFO_MESSAGES, errorKey);
    }

    protected void stuffBindingErrorsBackIntoModel(String modelName, Object modelObject, Model springModel, BindingResult bindingResult) {
        springModel.addAttribute("org.springframework.validation.BindingResult." + modelName, bindingResult);
        springModel.addAttribute(modelName, modelObject);
    }
}
