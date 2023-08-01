package tv.memoryleakdeath.hex.common.pojo;

import java.io.Serializable;

public class HexOAuthAuthorizationConsentPojo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String registeredClientId;
    private String principalName;
    private String authorities;

    public String getRegisteredClientId() {
        return registeredClientId;
    }

    public void setRegisteredClientId(String registeredClientId) {
        this.registeredClientId = registeredClientId;
    }

    public String getPrincipalName() {
        return principalName;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    public String getAuthorities() {
        return authorities;
    }

    public void setAuthorities(String authorities) {
        this.authorities = authorities;
    }
}
