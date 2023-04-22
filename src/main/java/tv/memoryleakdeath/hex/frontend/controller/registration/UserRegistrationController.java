package tv.memoryleakdeath.hex.frontend.controller.registration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import tv.memoryleakdeath.hex.backend.dao.security.AuthenticationDao;
import tv.memoryleakdeath.hex.backend.security.HexTotpService;
import tv.memoryleakdeath.hex.backend.security.UserAuthService;
import tv.memoryleakdeath.hex.common.pojo.Auth;
import tv.memoryleakdeath.hex.common.pojo.TfaType;

@Controller
@RequestMapping("/registration")
public class UserRegistrationController {
    private static final Logger logger = LoggerFactory.getLogger(UserRegistrationController.class);
    private static final String VM = "/registration/registration";
    private static final String URL = "/registration/";

    @Autowired
    private AuthenticationDao authDao;

    @Autowired
    private HexTotpService totpService;

    @Autowired
    private UserAuthService userAuthService;

    @GetMapping("/")
    public String registerNewUser(HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        return VM;
    }

    @PostMapping("/create")
    public String createNewUser(HttpServletRequest request, Model model, @ModelAttribute UserRegistrationModel userModel, RedirectAttributes redirectAttributes, BindingResult bindingResult) {
        String returnView;
        try {
            if (authDao.checkUsernameExists(userModel.getUsername())) {
                logger.error("Unable to create user: {} username already exists!", userModel.getUsername());
                redirectAttributes.addFlashAttribute("userModel", userModel);
                returnView = "redirect: " + URL;
            } else {
                String encodedPassword = userAuthService.encodePassword(userModel.getPassword());
                authDao.createUserInitial(userModel.getUsername(), encodedPassword);
                if (Boolean.TRUE.equals(userModel.getUseTfa())) {
                    return createTfa(request, model, userModel.getUsername());
                }
                returnView = "redirect: /login";
            }
        } catch (Exception e) {
            logger.error("Unable to create new user!", e);
            returnView = "redirect: " + URL;
        }
        return returnView;
    }

    @PostMapping("/createtfa")
    public String createTfa(HttpServletRequest request, Model model, @RequestParam(name = "u", required = true) String username) {
        String returnView;
        try {
            String secret = totpService.generateNewSecret();
            if (!authDao.updateUserTfa(username, false, secret, TfaType.authapp)) {
                logger.error("Unable to update user's tfa settings!");
                returnView = "redirect: " + URL;
            } else {
                String dataUrl = totpService.generateQrCode(secret, username);
                model.addAttribute("qrcode", dataUrl);
                model.addAttribute("username", username);
                returnView = "/registration/tfa";
            }
        } catch (Exception e) {
            logger.error("Unable to generate qr code for 2fa for user: " + username, e);
            returnView = "redirect: " + URL;
        }
        return returnView;
    }

    @PostMapping("/validatetfa")
    public String validateTfa(HttpServletRequest request, Model model, @RequestParam(name = "code", required = true) String validationCode,
            @RequestParam(name = "u", required = true) String username) {
        String returnView;
        try {
            Auth userAuth = authDao.getUserByUsername(username);
            if (userAuth != null && totpService.verify(userAuth.getSecret(), validationCode) && authDao.updateUserTfa(username, true)) {
                returnView = "redirect: /login";
            } else {
                logger.error("Unable to verify user's inital 2fa code for user: {}", username);
                returnView = "redirect: " + URL;
            }
        } catch (Exception e) {
            logger.error("Unable to fetch user information to verify 2fa code for user: " + username, e);
            returnView = "redirect: " + URL;
        }
        return returnView;
    }
}
