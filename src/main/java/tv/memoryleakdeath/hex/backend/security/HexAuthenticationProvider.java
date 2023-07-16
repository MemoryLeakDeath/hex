package tv.memoryleakdeath.hex.backend.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import tv.memoryleakdeath.hex.backend.dao.security.AuthenticationDao;
import tv.memoryleakdeath.hex.common.pojo.Auth;

public class HexAuthenticationProvider extends DaoAuthenticationProvider {

    @Autowired
    private AuthenticationDao authDao;

    @Autowired
    private HexTotpService totpService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!supports(authentication.getClass())) {
            return null;
        }
        Auth user = null;
        try {
            user = authDao.getUserByUsername(authentication.getName());
            if (user == null) {
                throw new BadCredentialsException("User does not exist!");
            }
        } catch (Exception e) {
            throw new BadCredentialsException("User does not exist or sql error!");
        }

        HexWebAuthenticationDetails details = (HexWebAuthenticationDetails) authentication.getDetails();
        if (details == null) {
            authDao.updateFailedAttempts(user.getUsername(), false);
            throw new BadCredentialsException("No User Details were created!");
        }

        if (Boolean.TRUE.equals(user.getUseTfa())) {
            String authCode = details.getVerificationCode();
            if (!totpService.verify(user.getSecret(), authCode)) {
                authDao.updateFailedAttempts(user.getUsername(), false);
                throw new BadCredentialsException("2FA code incorrect");
            }
        }
        try {
            Authentication check = super.authenticate(authentication);
            if (!check.isAuthenticated()) {
                authDao.updateFailedAttempts(user.getUsername(), false);
            } else {
                // successful authentication
                authDao.updateFailedAttempts(user.getUsername(), true);
            }
            return check;
        } catch (AuthenticationException e) {
            authDao.updateFailedAttempts(user.getUsername(), false);
            throw e;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }

}
