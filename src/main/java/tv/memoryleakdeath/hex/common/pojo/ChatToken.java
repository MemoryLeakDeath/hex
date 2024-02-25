package tv.memoryleakdeath.hex.common.pojo;

import java.io.Serializable;

import tv.memoryleakdeath.hex.common.ChatTokenType;

public class ChatToken implements Serializable {

    private static final long serialVersionUID = 1L;
    private String rawToken;
    private String parsedToken;
    private ChatTokenType type;

    public String getRawToken() {
        return rawToken;
    }

    public void setRawToken(String token) {
        this.rawToken = token;
    }

    public ChatTokenType getType() {
        return type;
    }

    public void setType(ChatTokenType type) {
        this.type = type;
    }

    public String getParsedToken() {
        return parsedToken;
    }

    public void setParsedToken(String parsedToken) {
        this.parsedToken = parsedToken;
    }
}
