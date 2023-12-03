package tv.memoryleakdeath.hex.common.pojo;

import java.io.Serializable;

public class ChatMessageDetails implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    private String senderDisplayName;

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
}
