package tv.memoryleakdeath.hex.backend.security;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class HexRememberMeToken implements Authentication {

    private static final long serialVersionUID = 1L;
    private Collection<? extends GrantedAuthority> authorities;
    private Object details;
    private Object principal;
    private Object credentials;
    private boolean authenticated;

    public HexRememberMeToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
        this.principal = principal;
        this.credentials = credentials;
        this.authenticated = true;
    }

    @Override
    public String getName() {
        return "rememberme";
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getDetails() {
        return details;
    }

    public void setDetails(Object details) {
        this.details = details;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

}
