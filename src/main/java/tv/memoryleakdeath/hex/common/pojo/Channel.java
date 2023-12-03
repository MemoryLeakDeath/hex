package tv.memoryleakdeath.hex.common.pojo;

import java.io.Serializable;
import java.util.Date;

public class Channel implements Serializable {

    private static final long serialVersionUID = 1L;
    private String userId;
    private String name;
    private Boolean active;
    private String title;
    private Boolean live;
    private Date created;
    private Date lastUpdated;
    private ChannelSettings settings;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getLive() {
        return live;
    }

    public void setLive(Boolean live) {
        this.live = live;
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

    public ChannelSettings getSettings() {
        return settings;
    }

    public void setSettings(ChannelSettings settings) {
        this.settings = settings;
    }

}
