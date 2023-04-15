package tv.memoryleakdeath.hex.backend.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import tv.memoryleakdeath.hex.backend.dao.security.AuthenticationDao;
import tv.memoryleakdeath.hex.common.pojo.Auth;

@Service
public class HexUserDetailsService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(HexUserDetailsService.class);

    @Autowired
    private AuthenticationDao authDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails userDetails = null;
        try {
            Auth user = authDao.getUserByUsername(username);
            userDetails = User.withUsername(user.getUsername()).password(user.getPassword()).authorities(user.getRoles()).disabled(!user.getActive()).build();
        } catch (Exception e) {
            logger.error("User not found!  Authentication failed!");
            throw new UsernameNotFoundException("User not found", e);
        }
        if (userDetails == null) {
            logger.error("User not found!  Authentication failed!");
            throw new UsernameNotFoundException("User not found");
        }
        return userDetails;
    }

}
