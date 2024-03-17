package tv.memoryleakdeath.hex.common.pojo;

import java.io.Serializable;
import java.util.Date;

public class BanAudit implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String targetUserId;
    private String followerUserId;
    private String moderatorId;
    private String reason;
    private String action;
    private Date createdDate;
    private Date lastUpdatedDate;
    private Date timedOutExpirationDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getModeratorId() {
        return moderatorId;
    }

    public void setModeratorId(String moderatorId) {
        this.moderatorId = moderatorId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
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

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
