package tv.memoryleakdeath.hex.backend.security;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import jakarta.servlet.http.HttpServletRequest;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class HexWebAuthenticationDetails extends WebAuthenticationDetails {

    private static final long serialVersionUID = 1L;
    private String verificationCode;

    public HexWebAuthenticationDetails(HttpServletRequest request) {
        super(request);
        this.verificationCode = request.getParameter("2fa");
    }

    /*
     * This constructor is purely for supporting Jackson
     * deserialization/serialization as needed by OAuth2
     */
    @JsonCreator
    public HexWebAuthenticationDetails(@JsonProperty("remoteAddress") String remoteAddress,
            @JsonProperty("sessionId") String sessionId, @JsonProperty("verificationCode") String verificationCode) {
        super(remoteAddress, sessionId);
        this.verificationCode = verificationCode;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

}
