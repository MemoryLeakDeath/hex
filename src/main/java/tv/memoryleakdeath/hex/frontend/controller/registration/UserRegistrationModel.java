package tv.memoryleakdeath.hex.frontend.controller.registration;

import tv.memoryleakdeath.hex.common.pojo.Auth;

public class UserRegistrationModel extends Auth {

    private static final long serialVersionUID = 1L;

    private String captchaAnswer;
    private String displayName;
    private String email;

    public String getCaptchaAnswer() {
        return captchaAnswer;
    }

    public void setCaptchaAnswer(String captchaAnswer) {
        this.captchaAnswer = captchaAnswer;
    }

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
