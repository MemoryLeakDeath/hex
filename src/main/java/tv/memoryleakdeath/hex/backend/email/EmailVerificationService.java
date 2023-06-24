package tv.memoryleakdeath.hex.backend.email;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tv.memoryleakdeath.hex.backend.dao.email.EmailVerificationDao;

@Service
public class EmailVerificationService {
    private static final Logger logger = LoggerFactory.getLogger(EmailVerificationService.class);
    private static final int TOKEN_SIZE = 64;
    private static final long TOKEN_EXPIRATION_MINUTES = 15;

    @Autowired
    private EmailVerificationDao verificationDao;

    public String createNewVerificationToken(String userId) {
        byte[] token = new byte[TOKEN_SIZE];
        new SecureRandom().nextBytes(token);
        Date expirationDate = Date.from(Instant.now().plus(TOKEN_EXPIRATION_MINUTES, ChronoUnit.MINUTES));
        String encodedToken = Base64.getUrlEncoder().encodeToString(token);
        if (verificationDao.createNewToken(userId, encodedToken, expirationDate)) {
            return encodedToken;
        }
        return null;
    }

    public boolean isTokenValid(String userId, String token) {
        verificationDao.deleteExpired();
        return verificationDao.isVerified(userId, token);
    }
}
