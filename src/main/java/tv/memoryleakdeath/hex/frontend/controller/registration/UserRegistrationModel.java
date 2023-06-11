package tv.memoryleakdeath.hex.frontend.controller.registration;

import tv.memoryleakdeath.hex.common.pojo.Auth;

public class UserRegistrationModel extends Auth {

    private static final long serialVersionUID = 1L;

    private String captchaAnswer;

    public String getCaptchaAnswer() {
        return captchaAnswer;
    }

    public void setCaptchaAnswer(String captchaAnswer) {
        this.captchaAnswer = captchaAnswer;
    }
}
