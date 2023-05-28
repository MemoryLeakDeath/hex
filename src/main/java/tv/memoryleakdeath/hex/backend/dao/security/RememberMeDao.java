package tv.memoryleakdeath.hex.backend.dao.security;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import tv.memoryleakdeath.hex.backend.dao.mapper.RememberMeMapper;
import tv.memoryleakdeath.hex.common.pojo.RememberMe;

@Repository
public class RememberMeDao {
    private static final Logger logger = LoggerFactory.getLogger(RememberMeDao.class);
    private static final String[] COLUMN_NAMES = { "userid", "token", "lastused", "expirationdate" };

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String FIND_NON_EXPIRED_BY_SERIES_AND_TOKEN_SQL = """
                select %s from rememberme
                where token = ?
                and expirationdate > NOW()
            """.formatted(StringUtils.join(COLUMN_NAMES, ","));

    public RememberMe find(String token) {
        List<RememberMe> results = jdbcTemplate.query(FIND_NON_EXPIRED_BY_SERIES_AND_TOKEN_SQL, new RememberMeMapper(), token);
        if (results.isEmpty()) {
            logger.debug("[Remember Me] No remember me token found!");
            return null;
        }
        logger.debug("[Remember Me] Remember me token found.");
        return results.get(0);
    }

    @Transactional
    public boolean updateTokenAndLastUsed(String userId, String oldToken, String newToken) {
        logger.debug("[Remember Me] updating last used timestamp for user");
        String sql = "update rememberme set lastused = NOW(), token = ? where userid = ? and token = ?";
        int results = jdbcTemplate.update(sql, newToken, userId, oldToken);
        return (results > 0);
    }

    @Transactional
    public int bulkDeleteExpired() {
        String sql = "delete from rememberme where expirationdate < NOW()";
        int rows = jdbcTemplate.update(sql);
        logger.debug("[Remember Me] Bulk deleted {} expired tokens.", rows);
        return rows;
    }

    @Transactional
    public boolean create(RememberMe me) {
        logger.debug("[Remember me] Creating new token for user.");
        String sql = "insert into rememberme (userid, token, lastused, expirationdate) values (?::uuid,?,NOW(),?)";
        int rows = jdbcTemplate.update(sql, me.getUserId(), me.getToken(), me.getExpirationDate());
        return (rows > 0);
    }
}
