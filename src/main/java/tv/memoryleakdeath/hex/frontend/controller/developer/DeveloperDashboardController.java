package tv.memoryleakdeath.hex.frontend.controller.developer;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import tv.memoryleakdeath.hex.backend.dao.security.AuthenticationDao;
import tv.memoryleakdeath.hex.backend.dao.security.OAuth2ClientRepositoryDao;
import tv.memoryleakdeath.hex.common.ApiScopes;
import tv.memoryleakdeath.hex.common.pojo.GenericResponse;
import tv.memoryleakdeath.hex.common.pojo.HexRegisteredClientPojo;
import tv.memoryleakdeath.hex.frontend.controller.BaseFrontendController;
import tv.memoryleakdeath.hex.frontend.utils.ValidationUtils;

@Controller
@RequestMapping("/developer")
public class DeveloperDashboardController extends BaseFrontendController {
    private static final Logger logger = LoggerFactory.getLogger(DeveloperDashboardController.class);
    public static final Map<String, String> CLIENT_AUTH_METHODS = Map.of("text.oauth.method.basic",
            ClientAuthenticationMethod.CLIENT_SECRET_BASIC.getValue(), "text.oauth.method.post",
            ClientAuthenticationMethod.CLIENT_SECRET_POST.getValue(), "text.oauth.method.jwt",
            ClientAuthenticationMethod.CLIENT_SECRET_JWT.getValue());
    public static final Map<String, String> CLIENT_AUTH_TYPES = Map.of("text.oauth.type.authorizationcode",
            AuthorizationGrantType.AUTHORIZATION_CODE.getValue(), "text.oauth.type.refreshtoken",
            AuthorizationGrantType.REFRESH_TOKEN.getValue());
    private static final String URL = "/developer/";
    private static final int CLIENT_SECRET_SIZE = 150;

    @Autowired
    private HexRegisteredClientModelValidator<HexRegisteredClientModel> validator;

    @Autowired
    private OAuth2ClientRepositoryDao dao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationDao authDao;

    @GetMapping("/")
    public String view(HttpServletRequest request, Model model) {
        setPageTitle(request, model, "title.developerdashboard");
        setLayout(model, "layout/developer");
        return "developer/developer-home";
    }

    @GetMapping("/applications")
    public String viewApplications(HttpServletRequest request, Model model) {
        setPageTitle(request, model, "title.developerdashboard");
        setLayout(model, "layout/developer");
        String userId = getUserId(request);
        try {
            List<HexRegisteredClientPojo> userOwnedApplications = dao.getRegisteredClientsForDeveloper(userId);
            Map<String, Integer> userAuthorizations = dao.getCountOfUserAuthorizationsForDeveloperApps(userId);
            model.addAttribute("apps", userOwnedApplications);
            model.addAttribute("authCounts", userAuthorizations);
        } catch (Exception e) {
            logger.error("Unable to display user owned applications!", e);
            addErrorMessage(request, "text.error.systemerror");
        }
        return "developer/applications";
    }

    @PostMapping("/applications/new")
    public String newApplication(HttpServletRequest request, Model model) {
        setPageTitle(request, model, "title.developerdashboard");
        setLayout(model, "layout/developer");
        addPageJS(model, "/js/developer/applications-new.js");
        model.addAttribute("clientAuthMethods", CLIENT_AUTH_METHODS);
        model.addAttribute("clientAuthTypes", CLIENT_AUTH_TYPES);
        if (!model.containsAttribute("registeredClientModel") || model.getAttribute("registeredClientModel") == null) {
            model.addAttribute("registeredClientModel", new HexRegisteredClientModel());
        }
        model.addAttribute("scopes", ApiScopes.asList());
        return "developer/applications-new";
    }

    @PostMapping("/applications/add")
    public String createNewApplication(HttpServletRequest request, Model model,
            @ModelAttribute HexRegisteredClientModel clientModel, BindingResult bindingResult) {
        String userId = getUserId(request);
        try {
            validator.validate(request, clientModel, bindingResult, false);
            if (bindingResult.hasErrors()) {
                logger.error("[REGISTER THIRD PARTY APPLICATION] Validation errors attempting to register application");
                stuffBindingErrorsBackIntoModel("registeredClientModel", clientModel, model, bindingResult);
                return newApplication(request, model);
            }
            // translate list of scopes to comma-separated string of scopes
            clientModel.setScopes(
                    clientModel.getSelectedScopes().stream().collect(Collectors.joining(",")));
            if (!dao.insert(clientModel, userId)) {
                logger.error("Unable to save new client application!");
                addErrorMessage(request, "text.error.systemerror");
                return newApplication(request, model);
            } else {
                logger.info("[REGISTER THIRD PARTY APPLICATION] New application registered: {}",
                        clientModel.getClientName());
                addSuccessMessage(request, "developer.applications.text.success.newclientregistered");
                return viewApplications(request, model);
            }
        } catch (Exception e) {
            logger.error(
                    "[REGISTER THIRD PARTY APPLICATION] Error occurred while attempting to register new application!",
                    e);
            addErrorMessage(request, "text.error.systemerror");
            return "redirect:" + URL;
        }
    }

    @PostMapping("/applications/generatesecret")
    public String generateClientSecret(HttpServletRequest request, Model model) {
        setLayout(model, "layout/minimal");
        try {
            model.addAttribute("generatedSecret", ValidationUtils.generateCompliantPassword(CLIENT_SECRET_SIZE));
        } catch (Exception e) {
            logger.error("Unable to generate a client secret!", e);
        }
        return "developer/generate-client-secret-popover";
    }

    @PostMapping("/applications/regeneratesecret")
    @ResponseBody
    public GenericResponse<String> regenerateClientSecret(HttpServletRequest request, Model model) {
        GenericResponse<String> response = new GenericResponse<>();
        try {
            String generatedPassword = ValidationUtils.generateCompliantPassword(CLIENT_SECRET_SIZE);
            response.setData(generatedPassword);
        } catch (Exception e) {
            logger.error("Unable to regenerate client secret!", e);
        }
        return response;
    }

    @PostMapping("/applications/changesecret")
    public String changeClientSecret(HttpServletRequest request, Model model,
            @RequestParam(name = "id", required = true) String id) {
        setPageTitle(request, model, "title.developerdashboard");
        setLayout(model, "layout/developer");
        addPageJS(model, "/js/developer/applications-change-secret.js");
        model.addAttribute("id", id);
        model.addAttribute("currentPassword", "");
        model.addAttribute("clientSecret", "");
        if (model.getAttribute("clientSecretModel") == null) {
            model.addAttribute("clientSecretModel", new UpdateApplicationClientSecretModel());
        }
        return "developer/applications-change-secret";
    }

    @PostMapping("/applications/updatesecret")
    public String updateClientSecret(HttpServletRequest request, Model model,
            @ModelAttribute UpdateApplicationClientSecretModel clientSecretModel,
            BindingResult bindingResult) {
        try {
            if (StringUtils.isBlank(clientSecretModel.getId())) {
                logger.error("Cannot update client secret, id is null!");
                addErrorMessage(request, "text.error.systemerror");
                return viewApplications(request, model);
            }

            String id = clientSecretModel.getId();
            String secret = clientSecretModel.getClientSecret();
            String currentPassword = clientSecretModel.getCurrentPassword();

            validator.validateClientSecret(request, secret, bindingResult);
            if (bindingResult.hasErrors()) {
                logger.error("Cannot update client secret, validations do not pass!");
                stuffBindingErrorsBackIntoModel("clientSecretModel", clientSecretModel, model, bindingResult);
                return changeClientSecret(request, model, id);
            }
            if (ValidationUtils.isUserPasswordNotCorrect(getUserId(request), currentPassword, passwordEncoder,
                    authDao)) {
                logger.error("Cannot update client secret, user password entered is not correct!");
                bindingResult.rejectValue("currentPassword", "settings.password.currentpasswordincorrect");
                stuffBindingErrorsBackIntoModel("clientSecretModel", clientSecretModel, model, bindingResult);
                return changeClientSecret(request, model, id);
            }
            boolean success = dao.updateClientSecret(id, secret);
            if (!success) {
                logger.error("Unable to update client secret for client id: {}", id);
                addErrorMessage(request, "text.error.systemerror");
            } else {
                logger.info("Client application with id: {} had its client secret updated successfully", id);
                addSuccessMessage(request, "developer.applications.text.success.clientsecretupdated");
            }
        } catch (Exception e) {
            logger.error("Unable to update client secret for client id: " + clientSecretModel.getId(), e);
            addErrorMessage(request, "text.error.systemerror");
        }
        return viewApplications(request, model);
    }

    @PostMapping("/applications/delete")
    public String deleteApplication(HttpServletRequest request, Model model,
            @RequestParam(name = "id", required = true) String id) {
        try {
            boolean success = dao.deleteClient(id);
            if (!success) {
                logger.error("Unable to delete client application with id: {}", id);
                addErrorMessage(request, "text.error.systemerror");
            } else {
                logger.info("Client application with id: {} was successfully deleted!", id);
                addSuccessMessage(request, "developer.applications.text.success.clientapplicationdeleted");
            }
        } catch (Exception e) {
            logger.error("Unable to delete client application with id: " + id, e);
            addErrorMessage(request, "text.error.systemerror");
        }
        return viewApplications(request, model);
    }

    @PostMapping("/applications/edit")
    public String editApplication(HttpServletRequest request, Model model,
            @RequestParam(name = "id", required = true) String id) {
        setPageTitle(request, model, "title.developerdashboard");
        setLayout(model, "layout/developer");
        model.addAttribute("clientAuthMethods", CLIENT_AUTH_METHODS);
        model.addAttribute("clientAuthTypes", CLIENT_AUTH_TYPES);
        model.addAttribute("scopes", ApiScopes.asList());
        try {
            if (!model.containsAttribute("registeredClientModel")
                    || model.getAttribute("registeredClientModel") == null) {
                HexRegisteredClientPojo clientPojo = dao.findPojoById(id);
                model.addAttribute("registeredClientModel", buildModelFromPojo(clientPojo));
            }
        } catch (Exception e) {
            logger.error("Unable to display edit client application page for id: " + id, e);
            addErrorMessage(request, "text.error.systemerror");
            return viewApplications(request, model);
        }
        return "developer/applications-edit";
    }

    private HexRegisteredClientModel buildModelFromPojo(HexRegisteredClientPojo pojo)
            throws IllegalAccessException, InvocationTargetException {
        HexRegisteredClientModel model = new HexRegisteredClientModel();
        BeanUtils.copyProperties(model, pojo);
        String[] scopes = pojo.getScopes().split(",");
        model.setSelectedScopes(Arrays.asList(scopes));
        return model;
    }

    @PostMapping("/applications/update")
    public String updateApplication(HttpServletRequest request, Model model,
            @ModelAttribute HexRegisteredClientModel clientModel, BindingResult bindingResult) {
        try {
            validator.validate(request, clientModel, bindingResult, true);
            if (bindingResult.hasErrors()) {
                logger.error(
                        "[UPDATE THIRD PARTY APPLICATION] Validation errors attempting to update application with id: {}",
                        clientModel.getId());
                stuffBindingErrorsBackIntoModel("registeredClientModel", clientModel, model, bindingResult);
                return editApplication(request, model, clientModel.getId());
            }
            // translate list of scopes to comma-separated string of scopes
            clientModel.setScopes(clientModel.getSelectedScopes().stream().collect(Collectors.joining(",")));
            dao.save(clientModel);
            logger.info("[UPDATE THIRD PARTY APPLICATION] application updated: {}", clientModel.getId());
            addSuccessMessage(request, "developer.applications.text.success.clientupdated");
            return viewApplications(request, model);
        } catch (Exception e) {
            logger.error(
                    "[UPDATE THIRD PARTY APPLICATION] Error occurred while attempting to update application: "
                            + clientModel.getId(),
                    e);
            addErrorMessage(request, "text.error.systemerror");
            return "redirect:" + URL;
        }
    }
}
