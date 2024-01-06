package tv.memoryleakdeath.hex.frontend.pubsub;

import java.io.Serializable;

import tv.memoryleakdeath.hex.common.pojo.HexUser;

public class PubSubDetails implements Serializable {
    private static final long serialVersionUID = 1L;

    private String sessionId;
    private String destinationTopic;
    private HexUser user;

    public String getDestinationTopic() {
        return destinationTopic;
    }

    public void setDestinationTopic(String destinationTopic) {
        this.destinationTopic = destinationTopic;
    }

    public HexUser getUser() {
        return user;
    }

    public void setUser(HexUser user) {
        this.user = user;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
