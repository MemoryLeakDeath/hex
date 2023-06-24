package tv.memoryleakdeath.hex.frontend.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.LocaleResolver;

import jakarta.servlet.http.HttpServletRequest;
import tv.memoryleakdeath.hex.frontend.controller.interceptors.ThymeleafLayoutInterceptor;
import tv.memoryleakdeath.hex.frontend.utils.PageHelperUtil;

public abstract class BaseFrontendController {
    public static final String PAGE_TITLE = "pageTitle";
    public static final String PAGE_HELPER = "pageHelper";
    public static final String ERROR_MESSAGES = "pageErrors";
    public static final String INFO_MESSAGES = "pageInfos";
    public static final String WARNING_MESSAGES = "pageWarnings";
    public static final String SUCCESS_MESSAGES = "pageSuccesses";
    public static final String IMAGE_CAPTCHA = "imageCaptcha";
    public static final String AUDIO_CAPTCHA = "audioCaptcha";

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private LocaleResolver localeResolver;

    @Autowired
    private PageHelperUtil pageHelper;

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

    protected void addCommonModelAttributes(Model model) {
        model.addAttribute(PAGE_HELPER, pageHelper);
    }

    protected String getUsername(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            return principal.getName();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private void addMessage(Model model, String attribute, String key) {
        List<String> messages = (List<String>) model.getAttribute(attribute);
        if (messages == null) {
            messages = new ArrayList<>();
            model.addAttribute(attribute, messages);
        }
        messages.add(key);
    }

    protected void addErrorMessage(Model model, String errorKey) {
        addMessage(model, ERROR_MESSAGES, errorKey);
    }

    protected void addSuccessMessage(Model model, String errorKey) {
        addMessage(model, SUCCESS_MESSAGES, errorKey);
    }

    protected void addWarningMessage(Model model, String errorKey) {
        addMessage(model, WARNING_MESSAGES, errorKey);
    }

    protected void addInfoMessage(Model model, String errorKey) {
        addMessage(model, INFO_MESSAGES, errorKey);
    }

    protected void stuffErrorsBackIntoModel(String modelName, Object modelObject, Model springModel, BindingResult bindingResult) {
        springModel.addAttribute("org.springframework.validation.BindingResult." + modelName, bindingResult);
        springModel.addAttribute(modelName, modelObject);
    }
}
