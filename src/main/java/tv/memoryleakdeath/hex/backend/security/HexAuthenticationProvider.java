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
        Auth user = null;
        try {
            user = authDao.getUserByUsername(authentication.getName());
            if (user == null) {
                throw new BadCredentialsException("User does not exist!");
            }
        } catch (Exception e) {
            throw new BadCredentialsException("User does not exist or sql error!");
        }

        if (Boolean.TRUE.equals(user.getUseTfa())) {
            String authCode = ((HexWebAuthenticationDetails) authentication.getDetails()).getVerificationCode();
            if (!totpService.verify(user.getSecret(), authCode)) {
                throw new BadCredentialsException("2FA code incorrect");
            }
        }
        return super.authenticate(authentication);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}
