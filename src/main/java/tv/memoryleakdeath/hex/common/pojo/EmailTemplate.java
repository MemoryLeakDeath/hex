package tv.memoryleakdeath.hex.common.pojo;

import java.io.Serializable;
import java.util.Locale;

public class EmailTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private EmailTemplateType type;
    private Locale locale;
    private String subject;
    private String body;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public EmailTemplateType getType() {
        return type;
    }

    public void setType(EmailTemplateType type) {
        this.type = type;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
