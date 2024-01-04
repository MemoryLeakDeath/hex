package tv.memoryleakdeath.hex.frontend.utils;

import java.security.Principal;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import jakarta.servlet.http.HttpServletRequest;
import tv.memoryleakdeath.hex.backend.dao.security.AuthenticationDao;
import tv.memoryleakdeath.hex.backend.dao.user.UserDetailsDao;
import tv.memoryleakdeath.hex.backend.security.HexRememberMeToken;
import tv.memoryleakdeath.hex.common.pojo.HexUser;
import tv.memoryleakdeath.hex.common.pojo.UserDetails;

public final class UserUtils {

    private UserUtils() {

    }

    public static HexUser getUserDetailsFromSession(HttpServletRequest request) {
        Authentication token = (Authentication) request.getUserPrincipal();
        if (token != null && token.getPrincipal() != null) {
            return (HexUser) token.getPrincipal();
        }
        return null;
    }

    public static String getUserId(HttpServletRequest request) {
        HexUser user = getUserDetailsFromSession(request);
        if (user != null) {
            return user.getId();
        }
        return null;
    }

    public static String getDisplayName(HttpServletRequest request) {
        HexUser user = getUserDetailsFromSession(request);
        if (user != null) {
            return user.getDisplayName();
        }
        return null;
    }

    public static void updateUserDetailsInCurrentSession(HttpServletRequest request, UserDetailsDao userDetailsDao) {
        HexUser user = getUserDetailsFromSession(request);
        if (user == null) {
            return;
        }
        UserDetails details = userDetailsDao.findById(user.getId());
        user.setDisplayName(details.getDisplayName());
        user.setGravatarId(details.getGravatarId());
    }

    public static HexUser getUserFromSpringPrincipal(Principal principal, AuthenticationDao authDao) {
        HexUser token = null;
        if (principal instanceof HexRememberMeToken) {
            token = (HexUser) ((HexRememberMeToken) principal).getPrincipal();
        } else if (principal instanceof UsernamePasswordAuthenticationToken) {
            token = (HexUser) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        }
        return token;
    }
}
