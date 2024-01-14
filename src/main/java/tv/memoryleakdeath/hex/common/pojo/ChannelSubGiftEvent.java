package tv.memoryleakdeath.hex.common.pojo;

import java.util.Set;

public class ChannelSubGiftEvent extends ChannelEvent {

    private static final long serialVersionUID = 1L;
    private Set<String> recipientUserIds;

    public Set<String> getRecipientUserIds() {
        return recipientUserIds;
    }

    public void setRecipientUserIds(Set<String> recipientUserIds) {
        this.recipientUserIds = recipientUserIds;
    }

    @Override
    public String toString() {
        return "ChannelSubGiftEvent [recipientUserIds=" + recipientUserIds + ", getType()=" + getType()
                + ", getSourceUserId()=" + getSourceUserId() + ", getChannelName()=" + getChannelName() + "]";
    }
}
