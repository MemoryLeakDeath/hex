package tv.memoryleakdeath.hex.common.pojo;

import java.io.Serializable;
import java.util.Date;

public class MessageEmote implements Serializable {

    private static final long serialVersionUID = 1L;
    private String id;
    private String userId;
    private boolean active;
    private boolean allowed;
    private String prefix;
    private String tag;
    private boolean subOnly;
    private boolean allowGlobal;
    private String smallImageFilename;
    private String smallImageUrl;
    private String smallImageType;
    private String largeImageFilename;
    private String largeImageUrl;
    private String largeImageType;
    private Date created;
    private Date lastUpdated;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isAllowed() {
        return allowed;
    }

    public void setAllowed(boolean allowed) {
        this.allowed = allowed;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean isSubOnly() {
        return subOnly;
    }

    public void setSubOnly(boolean subOnly) {
        this.subOnly = subOnly;
    }

    public boolean isAllowGlobal() {
        return allowGlobal;
    }

    public void setAllowGlobal(boolean allowGlobal) {
        this.allowGlobal = allowGlobal;
    }

    public String getSmallImageFilename() {
        return smallImageFilename;
    }

    public void setSmallImageFilename(String smallImageFilename) {
        this.smallImageFilename = smallImageFilename;
    }

    public String getSmallImageUrl() {
        return smallImageUrl;
    }

    public void setSmallImageUrl(String smallImageUrl) {
        this.smallImageUrl = smallImageUrl;
    }

    public String getSmallImageType() {
        return smallImageType;
    }

    public void setSmallImageType(String smallImageType) {
        this.smallImageType = smallImageType;
    }

    public String getLargeImageFilename() {
        return largeImageFilename;
    }

    public void setLargeImageFilename(String largeImageFilename) {
        this.largeImageFilename = largeImageFilename;
    }

    public String getLargeImageUrl() {
        return largeImageUrl;
    }

    public void setLargeImageUrl(String largeImageUrl) {
        this.largeImageUrl = largeImageUrl;
    }

    public String getLargeImageType() {
        return largeImageType;
    }

    public void setLargeImageType(String largeImageType) {
        this.largeImageType = largeImageType;
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

    public boolean isMatching(String emote) {
        return (prefix + tag).equals(emote);
    }

}
