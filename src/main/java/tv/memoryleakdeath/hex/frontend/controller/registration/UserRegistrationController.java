package tv.memoryleakdeath.hex.frontend.controller.registration;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

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

import jakarta.servlet.http.HttpServletRequest;
import net.logicsquad.nanocaptcha.audio.AudioCaptcha;
import net.logicsquad.nanocaptcha.image.ImageCaptcha;
import tv.memoryleakdeath.hex.backend.dao.security.AuthenticationDao;
import tv.memoryleakdeath.hex.backend.dao.user.UserDetailsDao;
import tv.memoryleakdeath.hex.backend.email.EmailService;
import tv.memoryleakdeath.hex.backend.email.EmailVerificationService;
import tv.memoryleakdeath.hex.backend.gravatar.GravatarService;
import tv.memoryleakdeath.hex.backend.security.HexCaptchaService;
import tv.memoryleakdeath.hex.backend.security.HexTotpService;
import tv.memoryleakdeath.hex.backend.security.UserAuthService;
import tv.memoryleakdeath.hex.common.pojo.Auth;
import tv.memoryleakdeath.hex.common.pojo.TfaType;
import tv.memoryleakdeath.hex.common.pojo.UserDetails;
import tv.memoryleakdeath.hex.frontend.controller.BaseFrontendController;

@Controller
@RequestMapping("/registration")
public class UserRegistrationController extends BaseFrontendController {
    private static final Logger logger = LoggerFactory.getLogger(UserRegistrationController.class);
    private static final String VM = "/registration/registration";
    private static final String URL = "/registration/";

    @Autowired
    private AuthenticationDao authDao;

    @Autowired
    private HexTotpService totpService;

    @Autowired
    private UserAuthService userAuthService;

    @Autowired
    private HexCaptchaService captchaService;

    @Autowired
    private EmailVerificationService emailVerificationService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserDetailsDao userDetailsDao;

    @Autowired
    private UserRegistrationModelValidator<UserRegistrationModel> registrationValidator;

    @Autowired
    private GravatarService gravatarService;

    @GetMapping("/")
    public String registerNewUser(HttpServletRequest request, Model model) {
        addPageJS(model, "/js/registration/registration.js");
        if (!model.containsAttribute("userModel") || model.getAttribute("userModel") == null) {
            model.addAttribute("userModel", new UserRegistrationModel());
        }
        try {
            ImageCaptcha imageCaptcha = captchaService.generateImageCaptcha();
            AudioCaptcha audioCaptcha = captchaService.generateAudioCaptcha();
            request.getSession(false).setAttribute(IMAGE_CAPTCHA, imageCaptcha);
            request.getSession(false).setAttribute(AUDIO_CAPTCHA, audioCaptcha);

            String encodedCaptcha = getEncodedImageCaptcha(request, imageCaptcha);
            String encodedAudioCaptcha = getEncodedAudioCaptcha(request, audioCaptcha);
            model.addAttribute("imageCaptcha", "data:image/png;base64," + encodedCaptcha);
            model.addAttribute("audioCaptcha", "data:audio/wav;base64," + encodedAudioCaptcha);
        } catch (Exception e) {
            logger.error("Unable to display register new user page!", e);
            addErrorMessage(request, "text.error.systemerror");
        }
        return VM;
    }

    private String getEncodedImageCaptcha(HttpServletRequest request, ImageCaptcha imageCaptcha) {
        String encodedCaptcha = "";
        try (ByteArrayOutputStream imageOutputStream = new ByteArrayOutputStream()) {
            ImageIO.write(imageCaptcha.getImage(), "png", imageOutputStream);
            imageOutputStream.flush();
            byte[] captchaImageBytes = imageOutputStream.toByteArray();
            encodedCaptcha = Base64.getEncoder().encodeToString(captchaImageBytes);
        } catch (Exception e) {
            logger.error("Unable to convert image captcha to data URI", e);
            addErrorMessage(request, "text.error.systemerror");
        }
        return encodedCaptcha;
    }

    private String getEncodedAudioCaptcha(HttpServletRequest request, AudioCaptcha audioCaptcha) {
        String encodedCaptcha = "";
        try (AudioInputStream is = audioCaptcha.getAudio().getAudioInputStream(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            AudioSystem.write(is, Type.WAVE, out);
            out.flush();
            byte[] captchaAudioBytes = out.toByteArray();
            encodedCaptcha = Base64.getEncoder().encodeToString(captchaAudioBytes);
        } catch (Exception e) {
            logger.error("Unable to convert audio captcha to data URI", e);
            addErrorMessage(request, "text.error.systemerror");
        }
        return encodedCaptcha;
    }

    @PostMapping("/create")
    public String createNewUser(HttpServletRequest request, Model model, @ModelAttribute UserRegistrationModel userModel, BindingResult bindingResult) {
        String returnView;
        try {
            registrationValidator.validate(request, userModel, bindingResult);
            if (bindingResult.hasErrors()) {
                logger.debug("[Validation] User registration validation failed!");
                userModel.setCaptchaAnswer("");
                stuffBindingErrorsBackIntoModel("userModel", userModel, model, bindingResult);
                return registerNewUser(request, model);
            }

            String encodedPassword = userAuthService.encodePassword(userModel.getPassword());
            userModel.setId(authDao.createUserInitial(userModel.getUsername(), encodedPassword));
            if (!createUserDetails(request, userModel)) {
                logger.debug("[User Details] Unable to save user details!");
                returnView = "redirect: " + URL;
                return returnView;
            }

            if (Boolean.TRUE.equals(userModel.getUseTfa())) {
                return createTfa(request, model, userModel.getUsername());
            }
            addSuccessMessage(request, "registration.text.success.usercreated");
            returnView = successfulRegistration(request, userModel);
        } catch (Exception e) {
            logger.error("Unable to create new user!", e);
            addErrorMessage(request, "text.error.systemerror");
            returnView = "redirect: " + URL;
        }
        return returnView;
    }

    private boolean createUserDetails(HttpServletRequest request, UserRegistrationModel userRegistration) {
        if (userDetailsDao.isDisplayNameTaken(userRegistration.getDisplayName())) {
            addErrorMessage(request, "registration.text.error.uniquedisplayname");
            return false;
        }
        UserDetails details = new UserDetails();
        details.setDisplayName(userRegistration.getDisplayName());
        details.setEmail(userRegistration.getEmail());
        details.setUserId(userRegistration.getId());
        details.setGravatarId(gravatarService.getHash(details.getEmail()));
        return userDetailsDao.insertInitialDetails(details);
    }

    @PostMapping("/createtfa")
    public String createTfa(HttpServletRequest request, Model model, @RequestParam(name = "u", required = true) String username) {
        String returnView;
        try {
            String secret = totpService.generateNewSecret();
            String encryptedSecret = totpService.getEncryptedSecret(secret);
            if (!authDao.updateUserTfa(username, false, encryptedSecret, TfaType.authapp)) {
                logger.error("Unable to update user's tfa settings!");
                addErrorMessage(request, "text.error.systemerror");
                returnView = "redirect: " + URL;
            } else {
                String dataUrl = totpService.generateQrCode(secret, username);
                model.addAttribute("qrcode", dataUrl);
                model.addAttribute("username", username);
                returnView = "/registration/tfa";
            }
        } catch (Exception e) {
            logger.error("Unable to generate qr code for 2fa for user: " + username, e);
            addErrorMessage(request, "text.error.systemerror");
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
                addSuccessMessage(request, "registration.text.success.usercreated");
                returnView = successfulRegistration(request, null);
            } else {
                logger.error("Unable to verify user's inital 2fa code for user: {}", username);
                addErrorMessage(request, "registration.text.error.tfainvalid");
                returnView = "redirect: " + URL;
            }
        } catch (Exception e) {
            logger.error("Unable to fetch user information to verify 2fa code for user: " + username, e);
            addErrorMessage(request, "text.error.systemerror");
            returnView = "redirect: " + URL;
        }
        return returnView;
    }

    private String successfulRegistration(HttpServletRequest request, UserRegistrationModel model) {
        logger.debug("[Successful Registration] User created successfully, performing successful actions...");
        String token = emailVerificationService.createNewVerificationToken(model.getId());
        if (!emailService.sendVerificationEmail(model.getEmail(), model.getDisplayName(), token, request.getServerName(), String.valueOf(request.getServerPort()))) {
            logger.error("[Email Verification Service] Unable to send user initial email verification token.");
        }
        return "redirect: /login";
    }
}
