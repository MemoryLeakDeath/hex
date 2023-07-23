package tv.memoryleakdeath.hex.frontend.utils;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.servlet.http.HttpServletRequest;
import me.gosimple.nbvcxz.Nbvcxz;
import me.gosimple.nbvcxz.resources.ConfigurationBuilder;
import me.gosimple.nbvcxz.resources.Generator;
import me.gosimple.nbvcxz.resources.Generator.CharacterTypes;
import net.logicsquad.nanocaptcha.audio.AudioCaptcha;
import net.logicsquad.nanocaptcha.image.ImageCaptcha;
import tv.memoryleakdeath.hex.backend.dao.security.AuthenticationDao;
import tv.memoryleakdeath.hex.backend.dao.user.UserDetailsDao;
import tv.memoryleakdeath.hex.backend.security.HexCaptchaService;
import tv.memoryleakdeath.hex.common.pojo.Auth;
import tv.memoryleakdeath.hex.frontend.controller.BaseFrontendController;

public final class ValidationUtils {
    public static final Pattern VALID_DISPLAYNAME = Pattern.compile("^\\S*(\\S\\s{0,1})*\\S*$", Pattern.CASE_INSENSITIVE);
    public static final Pattern VALID_USERNAME = Pattern.compile("^[a-zA-Z0-9]+$", Pattern.CASE_INSENSITIVE);
    public static final int MAX_USERNAME_LENGTH = 50;
    public static final int MAX_DISPLAYNAME_LENGTH = 75;
    public static final int MAX_EMAIL_LENGTH = 100;
    public static final int MAX_PASSWORD_LENGTH = 100;
    public static final int MIN_PASSWORD_LENGTH = 10;
    public static final int GENERATED_PASSWORD_LENGTH = 50;

    private static EmailValidator emailValidator = EmailValidator.getInstance(false, false);
    private static Nbvcxz complexityChecker = new Nbvcxz(new ConfigurationBuilder().setMaxLength(MAX_PASSWORD_LENGTH).setMinimumEntropy(50d).createConfiguration());

    private ValidationUtils() {
    }

    public static boolean isDisplayNameInvalid(String displayName) {
        return StringUtils.isBlank(displayName) || !VALID_DISPLAYNAME.matcher(displayName).matches()
                || displayName.length() > MAX_DISPLAYNAME_LENGTH;
    }

    public static boolean isDisplayNameTaken(String displayName, String userId, UserDetailsDao userDetailsDao) {
        return userDetailsDao.isDisplayNameTaken(displayName, userId);
    }

    public static boolean isDisplayNameTaken(String displayName, UserDetailsDao userDetailsDao) {
        return userDetailsDao.isDisplayNameTaken(displayName);
    }

    public static boolean isDisplaynameTakenOrInvalid(String displayName, String userId, UserDetailsDao userDetailsDao) {
        return isDisplayNameInvalid(displayName) || isDisplayNameTaken(displayName, userId, userDetailsDao);
    }

    public static boolean isEmailInvalid(String email) {
        return StringUtils.isBlank(email) || !emailValidator.isValid(email) || email.length() > MAX_EMAIL_LENGTH;
    }

    public static boolean isCaptchaInvalid(HttpServletRequest request, String answer, HexCaptchaService captchaService) {
        return StringUtils.isBlank(answer)
                || (!captchaService.verifyImageCaptcha((ImageCaptcha) request.getSession(false).getAttribute(BaseFrontendController.IMAGE_CAPTCHA), answer)
                        && !captchaService.verifyAudioCaptcha((AudioCaptcha) request.getSession(false).getAttribute(BaseFrontendController.AUDIO_CAPTCHA), answer));
    }

    public static boolean isUsernameInvalid(String username) {
        return StringUtils.isBlank(username)
                || !VALID_USERNAME.matcher(username).matches()
                || username.length() > MAX_USERNAME_LENGTH;
    }

    public static boolean isUsernameTaken(String username, AuthenticationDao authDao) {
        return authDao.checkUsernameExists(username);
    }

    public static boolean isUsernameTakenOrInvalid(String username, AuthenticationDao authDao) {
        return isUsernameInvalid(username) || isUsernameTaken(username, authDao);
    }

    public static boolean isPasswordInvalid(String password) {
        return StringUtils.isBlank(password)
                || password.length() > MAX_PASSWORD_LENGTH
                || password.length() < MIN_PASSWORD_LENGTH;
    }

    public static boolean isPasswordNotComplexEnough(String password) {
        return !complexityChecker.estimate(password).isMinimumEntropyMet();
    }

    public static boolean isUserPasswordNotCorrect(String userId, String password, PasswordEncoder passwordEncoder, AuthenticationDao authDao) {
        Auth user = authDao.getActiveUserById(userId);
        return !passwordEncoder.matches(password, user.getPassword());
    }

    public static String generateCompliantPassword() {
        return Generator.generateRandomPassword(CharacterTypes.ALPHANUMERICSYMBOL, GENERATED_PASSWORD_LENGTH);
    }

}
