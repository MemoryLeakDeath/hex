package tv.memoryleakdeath.hex.frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController extends BaseFrontendController {

    @GetMapping("/")
    public String view() {
        return "admin";
    }
}
