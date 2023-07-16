package tv.memoryleakdeath.hex.frontend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        return "loginfull";
    }

    @GetMapping("/loginmodal")
    public String viewModal(HttpServletRequest request, Model model, @RequestParam(name = "error", required = false, defaultValue = "false") Boolean error) {
        model.addAttribute("error", error);
        setLayout(model, "layout/minimal");
        return "login";
    }

}
