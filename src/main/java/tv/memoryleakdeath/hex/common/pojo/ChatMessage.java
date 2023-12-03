package tv.memoryleakdeath.hex.common.pojo;

import java.io.Serializable;
import java.util.Date;

public class ChatMessage implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    private String messageId;
    private String senderId;
    private String channelId;
    private Date created;
    private Date lastUpdated;
    private Boolean visible;
    private String message;
    private String channelName;
    private ChatMessageDetails details;

    public ChatMessageDetails getDetails() {
        return details;
    }

    public void setDetails(ChatMessageDetails details) {
        this.details = details;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

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

    @Override
    public Object clone() throws CloneNotSupportedException {
        Object clone = super.clone();
        ChatMessage message = (ChatMessage) clone;
        if (getDetails() != null) {
            message.setDetails((ChatMessageDetails) getDetails().clone());
        }
        return message;
    }

}
