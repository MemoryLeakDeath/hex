package tv.memoryleakdeath.hex.frontend.controller.settings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import jakarta.servlet.http.HttpServletRequest;
import tv.memoryleakdeath.hex.backend.dao.user.UserDetailsDao;
import tv.memoryleakdeath.hex.frontend.utils.ValidationUtils;

@Component
public class BasicSettingsModelValidator<T extends BasicSettingsModel> {

    @Autowired
    private UserDetailsDao userDetailsDao;

    public void validate(HttpServletRequest request, T target, Errors errors) {
        if (ValidationUtils.isDisplayNameInvalid(target.getDisplayName())) {
            errors.rejectValue("displayName", "registration.text.error.displaynameinvalid");
        }

        if (ValidationUtils.isDisplayNameTaken(target.getDisplayName(), userDetailsDao)) {
            errors.rejectValue("displayName", "registration.text.error.uniquedisplayname");
        }

        if (ValidationUtils.isEmailInvalid(target.getEmail())) {
            errors.rejectValue("email", "registration.text.error.emailinvalid");
        }
    }
}
