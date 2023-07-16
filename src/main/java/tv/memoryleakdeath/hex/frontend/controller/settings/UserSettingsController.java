package tv.memoryleakdeath.hex.frontend.controller.settings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import tv.memoryleakdeath.hex.backend.dao.user.UserDetailsDao;
import tv.memoryleakdeath.hex.backend.email.EmailService;
import tv.memoryleakdeath.hex.backend.email.EmailVerificationService;
import tv.memoryleakdeath.hex.backend.gravatar.GravatarService;
import tv.memoryleakdeath.hex.common.pojo.UserDetails;
import tv.memoryleakdeath.hex.frontend.controller.BaseFrontendController;

@RequestMapping("/settings")
@Controller
public class UserSettingsController extends BaseFrontendController {
    private static final Logger logger = LoggerFactory.getLogger(UserSettingsController.class);

    @Autowired
    private BasicSettingsModelValidator<BasicSettingsModel> settingsValidator;

    @Autowired
    private UserDetailsDao userDetailsDao;

    @Autowired
    private GravatarService gravatarService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailVerificationService emailVerificationService;

    @GetMapping("/")
    public String view(HttpServletRequest request, Model model) {
        setPageTitle(request, model, "title.settings");
        setLayout(model, "layout/settings");
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
                    }
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

    private UserDetails populateBasicUserDetails(String email, String displayName, String userId) {
        UserDetails userDetails = new UserDetails();
        userDetails.setDisplayName(displayName);
        userDetails.setEmail(email);
        userDetails.setGravatarId(gravatarService.getHash(email));
        userDetails.setUserId(userId);
        return userDetails;
    }
}
