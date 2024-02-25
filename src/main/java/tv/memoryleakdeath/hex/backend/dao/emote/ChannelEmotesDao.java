package tv.memoryleakdeath.hex.backend.dao.emote;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import tv.memoryleakdeath.hex.backend.dao.mapper.ChannelEmoteMapper;
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
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

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

    private static final String FIND_ACTIVE_CHANNEL_APPROVED_EMOTE = """
            select %s from channelemotes
            where (prefix || tag) IN (:names)
            and active = true
            and allowed = true
            and approvalstatus = 'channel approved'
            and subonly = false
            and allowglobal = false
            """.formatted(StringUtils.join(COLUMNS, ","));

    public List<ChannelEmote> getAllActiveChannelApprovedEmotes(Set<String> emoteNames) {
        Map<String, Object> params = new HashMap<>();
        params.put("names", emoteNames);
        return namedParameterJdbcTemplate.query(FIND_ACTIVE_CHANNEL_APPROVED_EMOTE, params, new ChannelEmoteMapper());
    }
}
