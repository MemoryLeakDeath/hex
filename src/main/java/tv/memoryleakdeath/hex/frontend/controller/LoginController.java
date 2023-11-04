package tv.memoryleakdeath.hex.frontend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/")
public class LoginController extends BaseFrontendController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @GetMapping("/login")
    public String view(HttpServletRequest request, Model model, @RequestParam(name = "error", required = false, defaultValue = "false") Boolean error) {
        model.addAttribute("error", error);
        setLayout(model, "layout/main");
        setPageTitle(request, model, "title.login");
        request.getSession().setAttribute(BaseFrontendController.LOGIN_ERROR_RETURN, "/login?error=true");
        return "loginfull";
    }

    @GetMapping("/loginmodal")
    public String viewModal(HttpServletRequest request, Model model, @RequestParam(name = "error", required = false, defaultValue = "false") Boolean error) {
        model.addAttribute("error", error);
        setLayout(model, "layout/minimal");
        request.getSession().setAttribute(BaseFrontendController.LOGIN_ERROR_RETURN, "/loginmodal?error=true");
        return "login";
    }

    @GetMapping("/loginminimal")
    public String viewMinimal(HttpServletRequest request, Model model,
            @RequestParam(name = "error", required = false, defaultValue = "false") Boolean error) {
        model.addAttribute("error", error);
        setPageTitle(request, model, "title.login");
        setLayout(model, "layout/no-navigation");
        request.getSession().setAttribute(BaseFrontendController.LOGIN_ERROR_RETURN, "/loginminimal?error=true");
        return "loginfull";
    }

    @GetMapping("/logoutonly")
    public String logout(HttpServletRequest request, Model model) {
        setPageTitle(request, model, "title.logout");
        setLayout(model, "layout/no-navigation");
        try {
            request.logout();
            SecurityContextHolder.clearContext();
        } catch (Exception e) {
            logger.error("Unable to logout!", e);
        }
        return "logout/logout";
    }

}
