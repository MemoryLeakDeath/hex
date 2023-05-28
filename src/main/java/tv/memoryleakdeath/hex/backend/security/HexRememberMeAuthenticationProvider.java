package tv.memoryleakdeath.hex.backend.security;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import tv.memoryleakdeath.hex.backend.dao.security.RememberMeDao;
import tv.memoryleakdeath.hex.common.pojo.RememberMe;
import tv.memoryleakdeath.hex.utils.CryptoUtil;

public class HexRememberMeAuthenticationProvider implements AuthenticationProvider {
    private static final Logger logger = LoggerFactory.getLogger(HexRememberMeAuthenticationProvider.class);
    private static final int IV_SIZE = 16;
    private static final int SALT_SIZE = 16;
    private static final String TOKEN_ALGO = "AES";
    private static final String CYPHER_INSTANCE = "AES/CBC/PKCS5Padding";
    private static final String KEY_GEN_INSTANCE = "PBKDF2WithHmacSHA256";

    @Autowired
    @Qualifier("rememberMeKey")
    private String rememberMeKey;

    @Autowired
    private RememberMeDao rememberDao;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!supports(authentication.getClass())) {
            return null;
        }
        HexRememberMeToken token = (HexRememberMeToken) authentication;
        if (!isTokenValid(token.getCredentials().toString())) {
            throw new BadCredentialsException("[Remember Me] remember me token is not valid!");
        }
        return authentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (HexRememberMeToken.class.isAssignableFrom(authentication));
    }

    private String decryptToken(String encryptedToken)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(CYPHER_INSTANCE);
        ByteBuffer rawSecret = ByteBuffer.wrap(Base64.getDecoder().decode(encryptedToken.getBytes(StandardCharsets.UTF_8)));
        byte[] rawSalt = new byte[SALT_SIZE];
        rawSecret.get(rawSalt);
        byte[] rawIv = new byte[IV_SIZE];
        rawSecret.get(rawIv);
        cipher.init(Cipher.DECRYPT_MODE, CryptoUtil.generateKey(rawSalt, KEY_GEN_INSTANCE, rememberMeKey, TOKEN_ALGO), new IvParameterSpec(rawIv));
        byte[] secretText = new byte[rawSecret.remaining()];
        rawSecret.get(secretText);
        return new String(cipher.doFinal(secretText), StandardCharsets.UTF_8);
    }

    private boolean isTokenValid(String cookieEncryptedToken) {
        RememberMe rememberMe = rememberDao.find(cookieEncryptedToken);
        if (rememberMe == null) {
            return false;
        }
        try {
            String savedDecryptedToken = decryptToken(cookieEncryptedToken);
            String databaseDecryptedToken = decryptToken(rememberMe.getToken());
            return (savedDecryptedToken.equals(databaseDecryptedToken));
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeySpecException | IllegalBlockSizeException
                | BadPaddingException e) {
            logger.error("[Remember Me] Unable to decrypt remember me tokens", e);
        }
        return false;
    }

}
