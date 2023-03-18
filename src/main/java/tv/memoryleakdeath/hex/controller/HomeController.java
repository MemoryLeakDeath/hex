package tv.memoryleakdeath.hex.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class HomeController {

	@GetMapping("/")
	public String view(HttpServletRequest request, Model model) {
		model.addAttribute("helloMsg", "Hello World!");
		return "index";
	}
}
