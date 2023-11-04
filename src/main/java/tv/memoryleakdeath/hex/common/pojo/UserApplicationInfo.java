package tv.memoryleakdeath.hex.common.pojo;

import java.io.Serializable;
import java.util.Date;

public class UserApplicationInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String applicationId;
    private String applicationName;
    private Date expires;
    private Date lastUsed;

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public Date getExpires() {
        return expires;
    }

    public void setExpires(Date expires) {
        this.expires = expires;
    }

    public Date getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(Date lastUsed) {
        this.lastUsed = lastUsed;
    }
}
