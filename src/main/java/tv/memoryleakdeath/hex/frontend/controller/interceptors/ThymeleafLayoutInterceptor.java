package tv.memoryleakdeath.hex.frontend.controller.interceptors;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ThymeleafLayoutInterceptor implements HandlerInterceptor {
    private static final String DEFAULT_LAYOUT = "layout/main";
    private static final String DEFAULT_VIEW_VARIABLE_NAME = "view";
    private static final String LAYOUT_NAME_VARIABLE = "layout";

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView == null || !modelAndView.hasView()) {
            return;
        }
        String currentView = modelAndView.getViewName();
        if (isRedirectOrForward(currentView)) {
            return;
        }

        String layout = (String) modelAndView.getModel().getOrDefault(LAYOUT_NAME_VARIABLE, DEFAULT_LAYOUT);
        modelAndView.setViewName(layout);
        modelAndView.addObject(DEFAULT_VIEW_VARIABLE_NAME, currentView);
    }

    private boolean isRedirectOrForward(String viewName) {
        return (viewName.startsWith("redirect:") || viewName.startsWith("forward:"));
    }

}
