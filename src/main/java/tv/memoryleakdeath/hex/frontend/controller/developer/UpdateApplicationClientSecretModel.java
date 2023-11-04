package tv.memoryleakdeath.hex.frontend.controller.developer;

import java.io.Serializable;

public class UpdateApplicationClientSecretModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String clientSecret;
    private String currentPassword;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }
}
