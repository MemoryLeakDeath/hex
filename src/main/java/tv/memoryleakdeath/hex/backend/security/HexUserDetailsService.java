package tv.memoryleakdeath.hex.backend.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import tv.memoryleakdeath.hex.backend.dao.security.AuthenticationDao;
import tv.memoryleakdeath.hex.backend.dao.user.UserDetailsDao;
import tv.memoryleakdeath.hex.common.pojo.Auth;
import tv.memoryleakdeath.hex.common.pojo.HexUser;

@Service
public class HexUserDetailsService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(HexUserDetailsService.class);

    @Autowired
    private AuthenticationDao authDao;

    @Autowired
    private UserDetailsDao userDetailsDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        HexUser userDetails = null;
        try {
            Auth user = authDao.getUserByUsername(username);
            if (user == null) {
                logger.error("User not found!  Authentication failed!");
                throw new UsernameNotFoundException("User not found");
            }
            tv.memoryleakdeath.hex.common.pojo.UserDetails details = userDetailsDao.findById(user.getId());
            userDetails = new HexUser(user.getUsername(), user.getPassword(), user.getActive(), true, true, true, AuthorityUtils.createAuthorityList(user.getRoles()));
            userDetails.setId(user.getId());
            if (details != null) {
                userDetails.setDisplayName(details.getDisplayName());
                userDetails.setGravatarId(details.getGravatarId());
            }
        } catch (Exception e) {
            logger.error("User not found!  Authentication failed!");
            throw new UsernameNotFoundException("User not found", e);
        }
        return userDetails;
    }

}
