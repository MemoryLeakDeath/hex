package tv.memoryleakdeath.hex.common.pojo;

import java.io.Serializable;

public class UserDetailsDisplay implements Serializable {

    private static final long serialVersionUID = 1L;

    private String displayName;
    private String gravatarId;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getGravatarId() {
        return gravatarId;
    }

    public void setGravatarId(String gravatarId) {
        this.gravatarId = gravatarId;
    }
}
