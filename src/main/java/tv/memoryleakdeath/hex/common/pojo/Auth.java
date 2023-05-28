package tv.memoryleakdeath.hex.common.pojo;

import java.io.Serializable;
import java.util.Date;

public class Auth implements Serializable {

    private static final long serialVersionUID = 1L;
    private String id;
    private String username;
    private String password;
    private Boolean active;
    private String[] roles;
    private Integer failedAttempts;
    private Boolean useTfa;
    private TfaType tfaType;
    private String secret;
    private Date createdDate;
    private Date lastAttemptedLogin;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    public Integer getFailedAttempts() {
        return failedAttempts;
    }

    public void setFailedAttempts(Integer failedAttempts) {
        this.failedAttempts = failedAttempts;
    }

    public Boolean getUseTfa() {
        return useTfa;
    }

    public void setUseTfa(Boolean useTfa) {
        this.useTfa = useTfa;
    }

    public TfaType getTfaType() {
        return tfaType;
    }

    public void setTfaType(TfaType tfaType) {
        this.tfaType = tfaType;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Date getLastAttemptedLogin() {
        return lastAttemptedLogin;
    }

    public void setLastAttemptedLogin(Date lastAttemptedLogin) {
        this.lastAttemptedLogin = lastAttemptedLogin;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
