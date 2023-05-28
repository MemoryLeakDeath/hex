package tv.memoryleakdeath.hex.backend.security;

import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tv.memoryleakdeath.hex.backend.dao.security.AuthenticationDao;
import tv.memoryleakdeath.hex.backend.dao.security.RememberMeDao;
import tv.memoryleakdeath.hex.common.pojo.Auth;
import tv.memoryleakdeath.hex.common.pojo.RememberMe;
import tv.memoryleakdeath.hex.utils.CryptoUtil;

@Service
public class RememberMeService implements RememberMeServices {
    private static final Logger logger = LoggerFactory.getLogger(RememberMeService.class);
    private static final int IV_SIZE = 16;
    private static final int SALT_SIZE = 16;
    private static final int SECRET_TOKEN_SIZE = 64;
    private static final String TOKEN_ALGO = "AES";
    private static final String CYPHER_INSTANCE = "AES/CBC/PKCS5Padding";
    private static final String KEY_GEN_INSTANCE = "PBKDF2WithHmacSHA256";
    private static final int REMEMBER_ME_EXPIRY_DAYS = 60;
    public static final String REMEMBER_ME_PARAM = "rememberme";

    @Autowired
    @Qualifier("rememberMeKey")
    private String rememberMeKey;

    @Autowired
    private RememberMeDao rememberDao;

    @Autowired
    private AuthenticationDao authDao;

    @Override
    public Authentication autoLogin(HttpServletRequest request, HttpServletResponse response) {
        Cookie rememberMeCookie = getRememberMeCookie(request);
        if (rememberMeCookie != null) {
            logger.debug("[Remember Me] Found rememberme cookie, getting details...");
            RememberMe rememberMe = findDatabaseToken(rememberMeCookie.getValue());
            if (rememberMe != null) {
                Auth userAuth = authDao.getActiveUserById(rememberMe.getUserId());
                if (userAuth != null) {
                    Collection<GrantedAuthority> authorities = new ArrayList<>();
                    for (String role : userAuth.getRoles()) {
                        authorities.add(new SimpleGrantedAuthority(role));
                    }
                    logger.debug("[Remember Me] rememberme token valid!  Logging in user.");
                    HexRememberMeToken token = new HexRememberMeToken(userAuth.getUsername(), rememberMeCookie.getValue(), authorities);
                    return token;
                }
            }
        }
        return null;
    }

    @Override
    public void loginFail(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (REMEMBER_ME_PARAM.equals(cookie.getName())) {
                    logger.debug("[Remember Me] Clearing rememberme cookie from browser due to loginFail");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }
    }

    @Override
    public void loginSuccess(HttpServletRequest request, HttpServletResponse response, Authentication successfulAuthentication) {
        String param = request.getParameter(REMEMBER_ME_PARAM);
        if ("true".equalsIgnoreCase(param)) {
            logger.debug("[Remember Me] Generating new rememberme cookie for user login.");
            String newToken = generateToken();
            boolean success = false;
            RememberMe me = new RememberMe();
            if (newToken != null) {
                String userId = authDao.getUserIdForUsername(((User) successfulAuthentication.getPrincipal()).getUsername());
                me.setToken(newToken);
                me.setUserId(userId);
                me.setExpirationDate(getExpirationDate());
                success = rememberDao.create(me);
            }

            if (success) {
                Cookie rememberMeCookie = new Cookie(REMEMBER_ME_PARAM, me.getToken());
                rememberMeCookie.setPath("/");
                rememberMeCookie.setHttpOnly(true);
                rememberMeCookie.setSecure(true);
                rememberMeCookie.setMaxAge(60 * 60 * 24 * REMEMBER_ME_EXPIRY_DAYS);
                response.addCookie(rememberMeCookie);
            }
        }
    }

    private String generateToken() {
        try {
            byte[] salt = CryptoUtil.generateSalt(SALT_SIZE);
            Cipher cipher = Cipher.getInstance(CYPHER_INSTANCE);
            IvParameterSpec iv = CryptoUtil.generateIv(IV_SIZE);
            SecretKey tokenKey = CryptoUtil.generateKey(salt, KEY_GEN_INSTANCE, rememberMeKey, TOKEN_ALGO);
            cipher.init(Cipher.ENCRYPT_MODE, tokenKey, iv);
            byte[] secretToken = new byte[SECRET_TOKEN_SIZE];
            new SecureRandom().nextBytes(secretToken);
            byte[] cipherText = cipher.doFinal(secretToken);
            ByteBuffer encryptedText = ByteBuffer.allocate(SALT_SIZE + IV_SIZE + cipherText.length).put(salt).put(iv.getIV()).put(cipherText);
            return Base64.getEncoder().encodeToString(encryptedText.array());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException
                | InvalidAlgorithmParameterException e) {
            logger.error("[Remember Me] Unable to create a remember me token!");
        }
        return null;
    }

    private Date getExpirationDate() {
        return (Date.from(Instant.now().plus(REMEMBER_ME_EXPIRY_DAYS, ChronoUnit.DAYS)));
    }

    private Cookie getRememberMeCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (REMEMBER_ME_PARAM.equals(cookie.getName())) {
                    return cookie;
                }
            }
        }
        return null;
    }

    private RememberMe findDatabaseToken(String cookieToken) {
        return rememberDao.find(cookieToken);
    }
}
