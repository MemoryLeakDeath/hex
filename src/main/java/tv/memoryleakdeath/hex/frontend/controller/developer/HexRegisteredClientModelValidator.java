package tv.memoryleakdeath.hex.frontend.controller.developer;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import jakarta.servlet.http.HttpServletRequest;
import tv.memoryleakdeath.hex.frontend.utils.ValidationUtils;

@Component
public class HexRegisteredClientModelValidator<T extends HexRegisteredClientModel> {
    private static final int CLIENT_SECRET_MIN_LENGTH = 20;
    private static final int CLIENT_SECRET_MAX_LENGTH = 150;

    public void validate(HttpServletRequest request, T model, Errors errors, boolean skipClientSecretValidation) {
        if (StringUtils.isBlank(model.getClientName())) {
            errors.rejectValue("clientName", "developer.applications.text.error.clientnamerequired");
        }
        if (!skipClientSecretValidation) {
            validateClientSecret(request, model.getClientSecret(), errors);
        }
        if (!DeveloperDashboardController.CLIENT_AUTH_METHODS.containsValue(model.getClientAuthenticationMethods())) {
            errors.rejectValue("clientAuthenticationMethods",
                    "developer.applications.text.error.clientauthmethodinvalid");
        }
        if (!DeveloperDashboardController.CLIENT_AUTH_TYPES.containsValue(model.getAuthorizationGrantTypes())) {
            errors.rejectValue("authorizationGrantTypes", "developer.applications.text.error.authgranttypeinvalid");
        }
        if (StringUtils.isBlank(model.getRedirectUris())) {
            errors.rejectValue("redirectUris", "developer.applications.text.error.redirecturirequired");
        }
        if (model.getSelectedScopes() == null || model.getSelectedScopes().isEmpty()) {
            errors.rejectValue("selectedScopes", "developer.applications.text.error.mustselectascope");
        }
    }

    public void validateClientSecret(HttpServletRequest request, String secret, Errors errors) {
        if (StringUtils.isBlank(secret)) {
            errors.rejectValue("clientSecret", "developer.applications.text.error.clientsecretrequired");
        }
        if (secret != null
                && (secret.length() < CLIENT_SECRET_MIN_LENGTH || secret.length() > CLIENT_SECRET_MAX_LENGTH)) {
            errors.rejectValue("clientSecret", "developer.applications.text.error.clientsecretlength");
        }
        if (ValidationUtils.isPasswordNotComplexEnough(secret)) {
            errors.rejectValue("clientSecret", "developer.applications.text.error.clientsecretnotcomplexenough");
        }
    }
}
