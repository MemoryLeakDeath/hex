package tv.memoryleakdeath.hex.backend.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import tv.memoryleakdeath.hex.backend.dao.security.AuthenticationDao;

@Service
public class UserAuthService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationDao authDao;

    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public boolean updatePassword(String userId, String unencryptedPassword) {
        return authDao.updatePassword(userId, encodePassword(unencryptedPassword));
    }
}
