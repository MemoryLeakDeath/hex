package tv.memoryleakdeath.hex.frontend.controller.settings;

import java.security.Principal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tv.memoryleakdeath.hex.backend.dao.security.OAuth2AuthorizationConsentDao;
import tv.memoryleakdeath.hex.backend.dao.security.OAuth2AuthorizationDao;
import tv.memoryleakdeath.hex.backend.dao.user.UserDetailsDao;
import tv.memoryleakdeath.hex.backend.email.EmailService;
import tv.memoryleakdeath.hex.backend.email.EmailVerificationService;
import tv.memoryleakdeath.hex.backend.gravatar.GravatarService;
import tv.memoryleakdeath.hex.backend.security.UserAuthService;
import tv.memoryleakdeath.hex.common.pojo.UserApplicationInfo;
import tv.memoryleakdeath.hex.common.pojo.UserDetails;
import tv.memoryleakdeath.hex.frontend.controller.BaseFrontendController;
import tv.memoryleakdeath.hex.frontend.controller.api.interceptors.JwtTokenBlacklistCheckerFilter;
import tv.memoryleakdeath.hex.frontend.utils.UserUtils;

@RequestMapping("/settings")
@Controller
public class UserSettingsController extends BaseFrontendController {
    private static final Logger logger = LoggerFactory.getLogger(UserSettingsController.class);

    @Autowired
    private BasicSettingsModelValidator<BasicSettingsModel> settingsValidator;

    @Autowired
    private UpdatePasswordModelValidator<UpdatePasswordModel> updatePasswordValidator;

    @Autowired
    private UserDetailsDao userDetailsDao;

    @Autowired
    private GravatarService gravatarService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailVerificationService emailVerificationService;

    @Autowired
    private UserAuthService userAuthService;

    @Autowired
    private OAuth2AuthorizationConsentDao oauthConsentDao;

    @Autowired
    private OAuth2AuthorizationDao authDao;

    @Autowired
    private SessionRegistry sessionRegistry;

    @GetMapping("/")
    public String view(HttpServletRequest request, Model model) {
        setPageTitle(request, model, "title.settings");
        setLayout(model, "layout/settings");
        addPageJS(model, "/js/settings/user-settings.js");
        try {
            UserDetails userDetails = userDetailsDao.findById(getUserId(request));
            if (userDetails == null) {
                logger.error("Could not find User!");
                model.addAttribute("userModel", new BasicSettingsModel());
                addErrorMessage(request, "text.error.systemerror");
            } else {
                BasicSettingsModel settings = new BasicSettingsModel();
                settings.setDisplayName(userDetails.getDisplayName());
                settings.setEmail(userDetails.getEmail());
                model.addAttribute("userModel", settings);
                if (Boolean.FALSE.equals(userDetails.getEmailVerified())) {
                    model.addAttribute("notEmailVerified", true);
                    addInfoMessage(request, "settings.text.info.emailverified");
                }
            }
        } catch (Exception e) {
            logger.error("Unable to display user settings page!", e);
            addErrorMessage(request, "text.error.systemerror");
        }
        return "settings/user-settings";
    }

    @PostMapping("/updatebasic")
    public String updateBasicSettings(HttpServletRequest request, Model model, @ModelAttribute BasicSettingsModel settings, BindingResult bindingResult) {
        setPageTitle(request, model, "title.settings");
        setLayout(model, "layout/settings");
        addPageJS(model, "/js/settings/user-settings.js");
        model.addAttribute("userModel", settings);
        String userId = getUserId(request);
        try {
            settingsValidator.validate(request, settings, bindingResult);
            if (bindingResult.hasErrors()) {
                logger.debug("[Validate Basic Settings] Validation failed!");
                stuffBindingErrorsBackIntoModel("userModel", settings, model, bindingResult);
            } else {
                String originalEmail = userDetailsDao.getUserEmail(userId);
                UserDetails userDetails = populateBasicUserDetails(settings.getEmail(), settings.getDisplayName(), userId);
                if (!userDetailsDao.updateBasicDetails(userDetails)) {
                    logger.error("Unable to update basic user details, failure from Dao!");
                    addErrorMessage(request, "text.error.systemerror");
                } else {
                    if (originalEmail != null && !originalEmail.equals(userDetails.getEmail())) {
                        userDetailsDao.updateEmailVerified(userId, false);
                        model.addAttribute("notEmailVerified", true);
                        addInfoMessage(request, "settings.text.info.emailverified");
                    }
                    UserUtils.updateUserDetailsInCurrentSession(request, userDetailsDao);
                    addSuccessMessage(request, "settings.text.success.save");
                }
            }
        } catch (Exception e) {
            logger.error("Unable to update basic user settings!", e);
            addErrorMessage(request, "text.error.systemerror");
        }
        return "settings/user-settings";
    }

    @GetMapping("/resend")
    public ResponseEntity<Void> resendVerificationEmail(HttpServletRequest request, Model model) {
        ResponseEntity<Void> response;
        try {
            String userId = getUserId(request);
            UserDetails userDetails = userDetailsDao.findById(userId);
            String token = emailVerificationService.createNewVerificationToken(userId);
            if (!emailService.sendVerificationEmail(userDetails.getEmail(), userDetails.getDisplayName(), token, request.getServerName(), String.valueOf(request.getServerPort()))) {
                logger.error("[Email Verification Service] Unable to send user initial email verification token.");
                response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } else {
                response = new ResponseEntity<>(HttpStatus.OK);
            }
        } catch (Exception e) {
            logger.error("Unable to re-send verification email", e);
            response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    @GetMapping("/changepassword")
    public String changePassword(HttpServletRequest request, Model model) {
        setPageTitle(request, model, "title.settings");
        setLayout(model, "layout/settings");
        addPageJS(model, "/js/settings/changepassword.js");
        model.addAttribute("updatePasswordModel", new UpdatePasswordModel());
        return "settings/changepassword";
    }

    @PostMapping("/updatepassword")
    public String updatePassword(HttpServletRequest request, HttpServletResponse response, Model model,
            @ModelAttribute UpdatePasswordModel updatePasswordModel, BindingResult bindingResult) {
        setPageTitle(request, model, "title.settings");
        setLayout(model, "layout/settings");
        addPageJS(model, "/js/settings/changepassword.js");
        try {
            updatePasswordValidator.validate(request, updatePasswordModel, bindingResult);
            if (bindingResult.hasErrors()) {
                logger.debug("[Update Password Validation] validation failed!");
                model.addAttribute("updatePasswordModel", updatePasswordModel);
                updatePasswordModel.setCurrentPassword(null);
                stuffBindingErrorsBackIntoModel("updatePasswordModel", updatePasswordModel, model, bindingResult);
            } else {
                if (!userAuthService.updatePassword(getUserId(request), updatePasswordModel.getNewPassword())) {
                    addErrorMessage(request, "text.error.systemerror");
                    model.addAttribute("updatePasswordModel", new UpdatePasswordModel());
                    return logoutUser(request, response);
                }
                model.addAttribute("updatePasswordModel", new UpdatePasswordModel());
                addSuccessMessage(request, "settings.password.success.passwordchanged");
                return logoutUser(request, response);
            }
        } catch (Exception e) {
            logger.error("Unable to update user password!", e);
            addErrorMessage(request, "text.error.systemerror");
        }
        return "settings/changepassword";
    }

    @GetMapping("/applications")
    public String viewApplications(HttpServletRequest request, Model model) {
        setPageTitle(request, model, "title.settings");
        setLayout(model, "layout/settings");
        String userName = getUser(request).getUsername();
        try {
            List<UserApplicationInfo> info = oauthConsentDao.findApplicationInfoForUser(userName);
            model.addAttribute("applications", info);
        } catch (Exception e) {
            logger.error("Unable to display user applications!", e);
            addErrorMessage(request, "text.error.systemerror");
        }
        return "settings/applications";
    }

    @PostMapping("/applications/revoke")
    public void revokeApplicationAccess(HttpServletRequest request, HttpServletResponse response, Model model,
            Principal principal, @RequestParam(name = "id", required = true) String applicationId) {
        response.setHeader("HX-Redirect", request.getContextPath() + "/settings/applications");
        String userName = getUser(request).getUsername();
        try {
            List<String> blackListAuthTokens = authDao.getNonExpiredAccessTokensForApplicationAndUser(applicationId,
                    userName);
            oauthConsentDao.remove(applicationId, userName);
            JwtTokenBlacklistCheckerFilter.getTokenBlacklist().addAll(blackListAuthTokens);
            addSuccessMessage(request, "settings.applications.success.accessrevoked");
        } catch (Exception e) {
            logger.error("Unable to revoke application access for application: " + applicationId, e);
            addErrorMessage(request, "text.error.systemerror");
        }
    }


    private UserDetails populateBasicUserDetails(String email, String displayName, String userId) {
        UserDetails userDetails = new UserDetails();
        userDetails.setDisplayName(displayName);
        userDetails.setEmail(email);
        userDetails.setGravatarId(gravatarService.getHash(email));
        userDetails.setUserId(userId);
        return userDetails;
    }
}
