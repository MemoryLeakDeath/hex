package tv.memoryleakdeath.hex.backend.dao.email;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import tv.memoryleakdeath.hex.backend.dao.mapper.EmailTemplateMapper;
import tv.memoryleakdeath.hex.common.pojo.EmailTemplate;
import tv.memoryleakdeath.hex.common.pojo.EmailTemplateType;

@Repository
public class EmailTemplateDao {
    private static final Logger logger = LoggerFactory.getLogger(EmailTemplateDao.class);
    private static final String[] COLUMNS = { "id", "emailtype", "locale", "subject", "body" };

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final String GET_TEMPLATE_BY_LOCALE_OR_DEFAULT_SQL = """
            select %s from emailtemplates
            where emailtype = :emailType::email_template_types and locale = :locale
            UNION all
            select %s from emailtemplates
            where emailtype = :emailType::email_template_types and locale is null
            """.formatted(StringUtils.join(COLUMNS, ","), StringUtils.join(COLUMNS, ","));

    public EmailTemplate getTemplateByLocaleOrDefault(EmailTemplateType templateType, Locale locale) {
        if (templateType == null || locale == null) {
            return null;
        }

        Map<String, Object> params = new HashMap<>();
        params.put("emailType", templateType.name());
        params.put("locale", locale.toLanguageTag());
        List<EmailTemplate> rows = namedParameterJdbcTemplate.query(GET_TEMPLATE_BY_LOCALE_OR_DEFAULT_SQL, params, new EmailTemplateMapper());
        if (!rows.isEmpty()) {
            return rows.get(0);
        }
        logger.error("No email template of type: {} for locale: {} found!", templateType.name(), locale.toLanguageTag());
        return null;
    }
}
