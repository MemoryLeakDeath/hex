package tv.memoryleakdeath.hex.frontend.controller.registration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import jakarta.servlet.http.HttpServletRequest;
import tv.memoryleakdeath.hex.backend.dao.security.AuthenticationDao;
import tv.memoryleakdeath.hex.backend.dao.user.UserDetailsDao;
import tv.memoryleakdeath.hex.backend.security.HexCaptchaService;
import tv.memoryleakdeath.hex.frontend.utils.ValidationUtils;

@Component
public class UserRegistrationModelValidator<T extends UserRegistrationModel> {

    @Autowired
    private HexCaptchaService captchaService;

    @Autowired
    private AuthenticationDao authDao;

    @Autowired
    private UserDetailsDao userDetailsDao;

    public void validate(HttpServletRequest request, T target, Errors errors) {
        if (ValidationUtils.isCaptchaInvalid(request, target.getCaptchaAnswer(), captchaService)) {
            errors.rejectValue("captchaAnswer", "registration.text.error.captcha");
        }

        if (ValidationUtils.isUsernameInvalid(target.getUsername())) {
            errors.rejectValue("username", "registration.text.error.usernameinvalid");
        }

        if (ValidationUtils.isUsernameTaken(target.getUsername(), authDao)) {
            errors.rejectValue("username", "registration.text.error.usernameexists");
        }

        if (ValidationUtils.isPasswordInvalid(target.getPassword())) {
            errors.rejectValue("password", "registration.text.error.passwordinvalid");
        }

        if (ValidationUtils.isPasswordNotComplexEnough(target.getPassword())) {
            errors.rejectValue("password", "registration.text.error.passwordnotcomplex");
        }

        String displayName = target.getDisplayName();
        if (ValidationUtils.isDisplayNameInvalid(displayName)) {
            errors.rejectValue("displayName", "registration.text.error.displaynameinvalid");
        }

        if (ValidationUtils.isDisplayNameTaken(displayName, userDetailsDao)) {
            errors.rejectValue("displayName", "registration.text.error.uniquedisplayname");
        }

        if (ValidationUtils.isEmailInvalid(target.getEmail())) {
            errors.rejectValue("email", "registration.text.error.emailinvalid");
        }
    }
}
