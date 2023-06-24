package tv.memoryleakdeath.hex.frontend.controller.registration;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import jakarta.servlet.http.HttpServletRequest;
import net.logicsquad.nanocaptcha.image.ImageCaptcha;
import tv.memoryleakdeath.hex.backend.dao.security.AuthenticationDao;
import tv.memoryleakdeath.hex.backend.security.HexCaptchaService;
import tv.memoryleakdeath.hex.frontend.controller.BaseFrontendController;

@Component
public class UserRegistrationModelValidator<T extends UserRegistrationModel> {
    private static final Pattern VALID_USERNAME = Pattern.compile("^[a-zA-Z0-9]+$", Pattern.CASE_INSENSITIVE);
    private static final Pattern VALID_DISPLAYNAME = Pattern.compile("^\\S*(\\S\\s{0,1})*\\S*$", Pattern.CASE_INSENSITIVE);

    private EmailValidator emailValidator = EmailValidator.getInstance(false, false);

    @Autowired
    private HexCaptchaService captchaService;

    @Autowired
    private AuthenticationDao authDao;

    public void validate(HttpServletRequest request, T target, Errors errors) {
        if (StringUtils.isBlank(target.getCaptchaAnswer())
                || !captchaService.verifyImageCaptcha((ImageCaptcha) request.getSession(false).getAttribute(BaseFrontendController.IMAGE_CAPTCHA), target.getCaptchaAnswer())) {
            errors.rejectValue("captchaAnswer", "registration.text.error.captcha");
        }

        if (authDao.checkUsernameExists(target.getUsername())) {
            errors.rejectValue("username", "registration.text.error.usernameexists");
        }

        if (!VALID_USERNAME.matcher(target.getUsername()).matches()) {
            errors.rejectValue("username", "registration.text.error.usernameinvalid");
        }

        String displayName = target.getDisplayName();
        if (StringUtils.isBlank(displayName) || !VALID_DISPLAYNAME.matcher(displayName).matches()) {
            errors.rejectValue("displayName", "registration.text.error.displaynameinvalid");
        }

        if (StringUtils.isBlank(target.getEmail()) || !emailValidator.isValid(target.getEmail())) {
            errors.rejectValue("email", "registration.text.error.emailinvalid");
        }
    }
}