package tv.memoryleakdeath.hex.backend.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Service
public class HexTotpService {
    private static final Logger logger = LoggerFactory.getLogger(HexTotpService.class);
    private static final int SECRET_LENGTH = 64;
    private static final String ISSUER = "Hex";
    private static final int PERIOD_DISCREPANCY = 2;

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
        DefaultCodeVerifier verifier = new DefaultCodeVerifier(new DefaultCodeGenerator(), new SystemTimeProvider());
        verifier.setAllowedTimePeriodDiscrepancy(PERIOD_DISCREPANCY);
        return verifier.isValidCode(secret, code);
    }
}
