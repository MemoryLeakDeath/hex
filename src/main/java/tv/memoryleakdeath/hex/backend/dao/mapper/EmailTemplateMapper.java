package tv.memoryleakdeath.hex.backend.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;

import tv.memoryleakdeath.hex.common.pojo.EmailTemplate;
import tv.memoryleakdeath.hex.common.pojo.EmailTemplateType;

public class EmailTemplateMapper implements RowMapper<EmailTemplate> {

    @Override
    public EmailTemplate mapRow(ResultSet rs, int rowNum) throws SQLException {
        EmailTemplate emailTemplate = new EmailTemplate();
        emailTemplate.setBody(rs.getString("body"));
        emailTemplate.setId(rs.getInt("id"));
        String localeString = rs.getString("locale");
        if (StringUtils.isNotBlank(localeString)) {
            emailTemplate.setLocale(Locale.forLanguageTag(localeString));
        }
        String templateType = rs.getString("emailtype");
        emailTemplate.setType(EmailTemplateType.valueOf(templateType));
        emailTemplate.setSubject(rs.getString("subject"));
        return emailTemplate;
    }

}
