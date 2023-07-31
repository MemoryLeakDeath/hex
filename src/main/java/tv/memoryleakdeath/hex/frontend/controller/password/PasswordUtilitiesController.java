package tv.memoryleakdeath.hex.frontend.controller.password;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import tv.memoryleakdeath.hex.common.pojo.GenericResponse;
import tv.memoryleakdeath.hex.frontend.controller.BaseFrontendController;
import tv.memoryleakdeath.hex.frontend.controller.settings.PasswordComplexityResponse;
import tv.memoryleakdeath.hex.frontend.utils.ValidationUtils;

@Controller
@RequestMapping("/password")
public class PasswordUtilitiesController extends BaseFrontendController {
    private static final Logger logger = LoggerFactory.getLogger(PasswordUtilitiesController.class);
    
    @PostMapping(value = "/validatepasswordcomplexity", produces = "application/json")
    @ResponseBody
    public PasswordComplexityResponse calculatePasswordComplexity(HttpServletRequest request, @RequestParam(name = "password", required = true) String password) {
        PasswordComplexityResponse response = new PasswordComplexityResponse();
        try {
            response.setNotComplexEnough(ValidationUtils.isPasswordNotComplexEnough(password));
        } catch (Exception e) {
            logger.error("Unable to check password complexity!", e);
        }
        return response;
    }

    @PostMapping("/generatepassword")
    public String generatePassword(HttpServletRequest request, Model model) {
        setLayout(model, "layout/minimal");
        try {
            model.addAttribute("generatedPassword", ValidationUtils.generateCompliantPassword());
        } catch (Exception e) {
            logger.error("Unable to generate a password!", e);
        }
        return "password/generate-password-popover";
    }

    @PostMapping("/regeneratepassword")
    @ResponseBody
    public GenericResponse<String> regeneratePassword(HttpServletRequest request) {
        GenericResponse<String> response = new GenericResponse<>();
        try {
            response.setData(ValidationUtils.generateCompliantPassword());
        } catch (Exception e) {
            logger.error("Unable to regenerate password!", e);
        }
        return response;
    }

}
