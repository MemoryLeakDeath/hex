package tv.memoryleakdeath.hex.backend.security;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

import jakarta.servlet.http.HttpServletRequest;

public class HexWebAuthenticationDetails extends WebAuthenticationDetails {

    private static final long serialVersionUID = 1L;
    private String verificationCode;

    public HexWebAuthenticationDetails(HttpServletRequest request) {
        super(request);
        this.verificationCode = request.getParameter("2fa");
    }

    public String getVerificationCode() {
        return verificationCode;
    }

}
