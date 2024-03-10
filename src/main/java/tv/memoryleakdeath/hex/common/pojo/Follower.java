package tv.memoryleakdeath.hex.common.pojo;

import java.io.Serializable;
import java.util.Date;

public class Follower implements Serializable {
    private static final long serialVersionUID = 1L;

    private String targetUserId;
    private String followerUserId;
    private Boolean active;
    private Boolean banned;
    private Boolean timedOut;
    private Boolean notifications;
    private Date createdDate;
    private Date lastUpdatedDate;
    private Date timedOutExpirationDate;

    public String getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(String targetUserId) {
        this.targetUserId = targetUserId;
    }

    public String getFollowerUserId() {
        return followerUserId;
    }

    public void setFollowerUserId(String followerUserId) {
        this.followerUserId = followerUserId;
    }

    public Boolean getBanned() {
        return banned;
    }

    public void setBanned(Boolean banned) {
        this.banned = banned;
    }

    public Boolean getTimedOut() {
        return timedOut;
    }

    public void setTimedOut(Boolean timedOut) {
        this.timedOut = timedOut;
    }

    public Boolean getNotifications() {
        return notifications;
    }

    public void setNotifications(Boolean notifications) {
        this.notifications = notifications;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(Date lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public Date getTimedOutExpirationDate() {
        return timedOutExpirationDate;
    }

    public void setTimedOutExpirationDate(Date timedOutExpirationDate) {
        this.timedOutExpirationDate = timedOutExpirationDate;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

}
