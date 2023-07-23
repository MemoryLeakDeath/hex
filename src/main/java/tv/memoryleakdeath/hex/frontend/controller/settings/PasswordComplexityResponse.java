package tv.memoryleakdeath.hex.frontend.controller.settings;

import java.io.Serializable;

public class PasswordComplexityResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean isNotComplexEnough = true;

    public boolean isNotComplexEnough() {
        return isNotComplexEnough;
    }

    public void setNotComplexEnough(boolean isNotComplexEnough) {
        this.isNotComplexEnough = isNotComplexEnough;
    }
}
