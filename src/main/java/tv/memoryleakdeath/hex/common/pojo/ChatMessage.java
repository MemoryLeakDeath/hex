package tv.memoryleakdeath.hex.common.pojo;

import java.io.Serializable;

public class ChatMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private String message;
    private String channelName;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }
}
