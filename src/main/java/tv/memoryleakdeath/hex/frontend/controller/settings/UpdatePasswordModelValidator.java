package tv.memoryleakdeath.hex.frontend.controller.settings;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import jakarta.servlet.http.HttpServletRequest;
import tv.memoryleakdeath.hex.backend.dao.security.AuthenticationDao;
import tv.memoryleakdeath.hex.frontend.controller.BaseFrontendController;
import tv.memoryleakdeath.hex.frontend.utils.UserUtils;
import tv.memoryleakdeath.hex.frontend.utils.ValidationUtils;

@Component
public class UpdatePasswordModelValidator<T extends UpdatePasswordModel> {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationDao authenticationDao;

    public void validate(HttpServletRequest request, T target, Errors errors) {
        if (StringUtils.isBlank(target.getCurrentPassword())) {
            errors.rejectValue("currentPassword", "settings.password.currentpasswordrequired");
        }

        int numPasswordChangeAttempts = getPasswordChangeAttempts(request);
        if (ValidationUtils.isUserPasswordNotCorrect(UserUtils.getUserId(request), target.getCurrentPassword(), passwordEncoder, authenticationDao)) {
            errors.rejectValue("currentPassword", "settings.password.currentpasswordincorrect");
            setPasswordChangeAttempts(request, numPasswordChangeAttempts++);
        }

        if (ValidationUtils.isPasswordInvalid(target.getNewPassword())) {
            errors.rejectValue("newPassword", "settings.password.newpasswordinvalid");
        }

        if (ValidationUtils.isPasswordNotComplexEnough(target.getNewPassword())) {
            errors.rejectValue("newPassword", "settings.password.newpasswordnotcomplex");
        }
    }

    private int getPasswordChangeAttempts(HttpServletRequest request) {
        Integer sessionChangeAttemptCounter = (Integer) request.getSession().getAttribute(BaseFrontendController.PASSWORD_CHANGE_ATTEMPTS);
        if (sessionChangeAttemptCounter != null) {
            return sessionChangeAttemptCounter;
        }
        return 0;
    }

    private void setPasswordChangeAttempts(HttpServletRequest request, Integer attempts) {
        request.getSession().setAttribute(BaseFrontendController.PASSWORD_CHANGE_ATTEMPTS, attempts);
    }
}
