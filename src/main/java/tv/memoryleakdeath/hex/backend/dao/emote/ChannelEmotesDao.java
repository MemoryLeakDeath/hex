package tv.memoryleakdeath.hex.backend.dao.emote;

import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import tv.memoryleakdeath.hex.common.pojo.ChannelEmote;

@Repository
public class ChannelEmotesDao {
    private static final Logger logger = LoggerFactory.getLogger(ChannelEmotesDao.class);
    private static final String[] COLUMNS = { "id", "userid", "active", "allowed", "approvalstatus", "prefix", "tag",
            "name", "subonly", "allowglobal", "smallimagefilename", "smallimageurl", "smallimagetype",
            "largeimagefilename", "largeimageurl", "largeimagetype", "created", "lastupdated" };
    private static final String[] NAMED_PARAMETER_BEAN_COLUMNS = { ":id::UUID", ":userId::UUID", ":active", ":allowed",
            ":approvalStatus::approval_types", ":prefix", ":tag", ":name", ":subOnly", ":allowGlobal",
            ":smallImageFilename", ":smallImageUrl", ":smallImageType::image_types", ":largeImageFilename",
            ":largeImageUrl", ":largeImageType::image_types", ":created", ":lastUpdated" };
    
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    
    private static final String INSERT_SQL = """
            insert into channelemotes (%s) values (%s)
            """.formatted(StringUtils.join(COLUMNS, ","),
            StringUtils.join(NAMED_PARAMETER_BEAN_COLUMNS, ","));

    public boolean insertNewEmote(ChannelEmote newEmote) {
        if (newEmote == null) {
            return false;
        }
        newEmote.setId(UUID.randomUUID().toString());
        newEmote.setCreated(new Date());
        newEmote.setLastUpdated(new Date());
        newEmote.setApprovalStatus("pending");
        int rows = namedParameterJdbcTemplate.update(INSERT_SQL, new BeanPropertySqlParameterSource(newEmote));
        return (rows > 0);
    }
}
