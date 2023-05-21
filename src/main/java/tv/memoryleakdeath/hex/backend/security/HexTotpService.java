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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.util.Utils;
import tv.memoryleakdeath.hex.utils.CryptoUtil;

@Service
public class HexTotpService {
    private static final Logger logger = LoggerFactory.getLogger(HexTotpService.class);
    private static final int SECRET_LENGTH = 64;
    private static final String ISSUER = "Hex";
    private static final int PERIOD_DISCREPANCY = 2;
    private static final int IV_SIZE = 16;
    private static final int SALT_SIZE = 16;
    private static final String CYPHER_INSTANCE = "AES/CBC/PKCS5Padding";
    private static final String TOTP_KEY_GEN_INSTANCE = "PBKDF2WithHmacSHA256";

    @Autowired
    @Qualifier("totpKey")
    private String totpKey;

    public String generateNewSecret() {
        return new DefaultSecretGenerator(SECRET_LENGTH).generate();
    }

    public String generateQrCode(String secret, String username) {
        QrData data = new QrData.Builder().algorithm(HashingAlgorithm.SHA1).digits(6).issuer(ISSUER).label(username).period(30).secret(secret).build();
        QrGenerator generator = new ZxingPngQrGenerator();
        try {
            return Utils.getDataUriForImage(generator.generate(data), generator.getImageMimeType());
        } catch (QrGenerationException exception) {
            logger.error("Unable to generate QR Code for user: " + username, exception);
        }
        return null;
    }

    public boolean verify(String secret, String code) {
        if (StringUtils.isAnyBlank(secret, code)) {
            return false;
        }
        DefaultCodeVerifier verifier = new DefaultCodeVerifier(new DefaultCodeGenerator(), new SystemTimeProvider());
        verifier.setAllowedTimePeriodDiscrepancy(PERIOD_DISCREPANCY);
        try {
            String decryptedSecret = getDecryptedSecret(secret);
            return verifier.isValidCode(decryptedSecret, code);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException
                | InvalidKeySpecException e) {
            logger.error("Unable to decrypt TOTP secret!", e);
            return false;
        }
    }

    public String getEncryptedSecret(String unencryptedSecret)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException {
        Cipher cipher = Cipher.getInstance(CYPHER_INSTANCE);
        IvParameterSpec iv = CryptoUtil.generateIv(IV_SIZE);
        byte[] salt = CryptoUtil.generateSalt(SALT_SIZE);
        cipher.init(Cipher.ENCRYPT_MODE, CryptoUtil.generateKey(salt, TOTP_KEY_GEN_INSTANCE, totpKey, "AES"), iv);
        byte[] cipherText = cipher.doFinal(unencryptedSecret.getBytes(StandardCharsets.UTF_8));
        ByteBuffer encryptedText = ByteBuffer.allocate(SALT_SIZE + IV_SIZE + cipherText.length).put(salt).put(iv.getIV()).put(cipherText);
        return Base64.getEncoder().encodeToString(encryptedText.array());
    }

    private String getDecryptedSecret(String encryptedSecret)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException {
        Cipher cipher = Cipher.getInstance(CYPHER_INSTANCE);
        ByteBuffer rawSecret = ByteBuffer.wrap(Base64.getDecoder().decode(encryptedSecret.getBytes(StandardCharsets.UTF_8)));
        byte[] rawSalt = new byte[SALT_SIZE];
        rawSecret.get(rawSalt);
        byte[] rawIv = new byte[IV_SIZE];
        rawSecret.get(rawIv);
        cipher.init(Cipher.DECRYPT_MODE, CryptoUtil.generateKey(rawSalt, TOTP_KEY_GEN_INSTANCE, totpKey, "AES"), new IvParameterSpec(rawIv));
        byte[] secretText = new byte[rawSecret.remaining()];
        rawSecret.get(secretText);
        return new String(cipher.doFinal(secretText), StandardCharsets.UTF_8);
    }
}
