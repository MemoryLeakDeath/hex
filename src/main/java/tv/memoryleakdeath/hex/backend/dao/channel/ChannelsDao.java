package tv.memoryleakdeath.hex.backend.dao.channel;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import tv.memoryleakdeath.hex.utils.ChannelUtils;

@Repository
public class ChannelsDao {
    private static final Logger logger = LoggerFactory.getLogger(ChannelsDao.class);
    private static final String[] COLUMNS = { "userid", "name", "active", "title", "live", "settings", "created",
            "lastupdated" };

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean hasChannel(String userId) {
        if (StringUtils.isBlank(userId)) {
            return false;
        }
        String sql = "select count(*) from channels where userid = ?::uuid";
        int result = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return (result > 0);
    }

    public String getChannelName(String userId) {
        if (StringUtils.isBlank(userId)) {
            return null;
        }
        String sql = "select name from channels where userid = ?::uuid";
        List<String> results = jdbcTemplate.queryForList(sql, String.class, userId);
        if (results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }

    public boolean createBarebonesChannel(String userId, String displayName) {
        String sql = "insert into channels (userid, name, active, live, created, lastupdated) VALUES (?::uuid,?,false,false,NOW(),NOW())";
        String convertedDisplayName = ChannelUtils.convertToChannelName(displayName);
        int rowsAffected = jdbcTemplate.update(sql, userId, convertedDisplayName);
        return (rowsAffected > 0);
    }

}
