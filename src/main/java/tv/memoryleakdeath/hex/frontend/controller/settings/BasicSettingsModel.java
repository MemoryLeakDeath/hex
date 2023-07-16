package tv.memoryleakdeath.hex.frontend.controller.settings;

import java.io.Serializable;

public class BasicSettingsModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private String displayName;
    private String email;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
