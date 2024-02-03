package tv.memoryleakdeath.hex.frontend.controller.dashboard;

import java.io.Serializable;

import org.springframework.web.multipart.MultipartFile;

public class EmoteModel implements Serializable {
    private static final long serialVersionUID = 1L;

    private String tag;
    private String name;
    private Boolean subscriberOnly = Boolean.FALSE;
    private Boolean global = Boolean.FALSE;
    private Boolean allowed = Boolean.TRUE;
    private MultipartFile smallImage;
    private MultipartFile largeImage;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getSubscriberOnly() {
        return subscriberOnly;
    }

    public void setSubscriberOnly(Boolean subscriberOnly) {
        this.subscriberOnly = subscriberOnly;
    }

    public Boolean getGlobal() {
        return global;
    }

    public void setGlobal(Boolean global) {
        this.global = global;
    }

    public MultipartFile getSmallImage() {
        return smallImage;
    }

    public void setSmallImage(MultipartFile smallImage) {
        this.smallImage = smallImage;
    }

    public MultipartFile getLargeImage() {
        return largeImage;
    }

    public void setLargeImage(MultipartFile largeImage) {
        this.largeImage = largeImage;
    }

    public Boolean getAllowed() {
        return allowed;
    }

    public void setAllowed(Boolean allowed) {
        this.allowed = allowed;
    }
}
