package tv.memoryleakdeath.hex.common.pojo;

import java.io.Serializable;

import tv.memoryleakdeath.hex.common.ChannelEventType;

public class ChannelEvent implements Serializable {

    private static final long serialVersionUID = 1L;
    private ChannelEventType type;
    private String sourceUserId;
    private String channelName;

    public ChannelEventType getType() {
        return type;
    }

    public void setType(ChannelEventType type) {
        this.type = type;
    }

    public String getSourceUserId() {
        return sourceUserId;
    }

    public void setSourceUserId(String sourceUserId) {
        this.sourceUserId = sourceUserId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    @Override
    public String toString() {
        return "ChannelEvent [type=" + type + ", sourceUserId=" + sourceUserId + ", channelName=" + channelName + "]";
    }
}
