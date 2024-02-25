package tv.memoryleakdeath.hex.common.pojo;

import java.io.Serializable;
import java.util.List;

import tv.memoryleakdeath.hex.common.ChannelEventType;

public class ChatMessageDetails implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    private String senderDisplayName;
    private transient ChannelEventType eventType;
    private List<MessageEmote> emoteList;
    private List<ChatToken> tokenList;
    private String processedMessage;

    public String getSenderDisplayName() {
        return senderDisplayName;
    }

    public void setSenderDisplayName(String senderDisplayName) {
        this.senderDisplayName = senderDisplayName;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public ChannelEventType getEventType() {
        return eventType;
    }

    public void setEventType(ChannelEventType eventType) {
        this.eventType = eventType;
    }

    public List<MessageEmote> getEmoteList() {
        return emoteList;
    }

    public void setEmoteList(List<MessageEmote> emoteList) {
        this.emoteList = emoteList;
    }

    public List<ChatToken> getTokenList() {
        return tokenList;
    }

    public void setTokenList(List<ChatToken> tokenList) {
        this.tokenList = tokenList;
    }

    public String getProcessedMessage() {
        return processedMessage;
    }

    public void setProcessedMessage(String processedMessage) {
        this.processedMessage = processedMessage;
    }
}
