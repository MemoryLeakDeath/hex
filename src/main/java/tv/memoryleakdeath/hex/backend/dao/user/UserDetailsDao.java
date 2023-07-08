package tv.memoryleakdeath.hex.backend.dao.user;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import tv.memoryleakdeath.hex.backend.dao.mapper.UserDetailsMapper;
import tv.memoryleakdeath.hex.common.pojo.UserDetails;

@Repository
public class UserDetailsDao {
    private static final Logger logger = LoggerFactory.getLogger(UserDetailsDao.class);
    private static final String[] COLUMNS = { "userid", "displayname", "email", "emailverified", "lastupdated", "gravatarid" };

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String GET_BY_ID_SQL = """
            select %s from userdetails where userid = ?::uuid
            """.formatted(StringUtils.join(COLUMNS, ","));

    public UserDetails findById(String userId) {
        if (userId == null) {
            return null;
        }
        List<UserDetails> results = jdbcTemplate.query(GET_BY_ID_SQL, new UserDetailsMapper(), userId);
        if (!results.isEmpty()) {
            return results.get(0);
        }
        logger.debug("Unable to find userdetails for userid: {}", userId);
        return null;
    }

    @Transactional
    public boolean insertInitialDetails(UserDetails details) {
        String sql = "insert into userdetails (userid, displayname, email, emailverified, lastupdated, gravatarid) values (?::uuid,?,?,false,now(),?)";
        int rows = jdbcTemplate.update(sql, details.getUserId(), details.getDisplayName(), details.getEmail(), details.getGravatarId());
        return (rows > 0);
    }

    @Transactional
    public boolean updateDetails(UserDetails details) {
        String sql = "update from userdetails set displayname = ?, email = ?, lastupdated = now(), gravatarid = ? where userid = ?";
        int rows = jdbcTemplate.update(sql, details.getDisplayName(), details.getEmail(), details.getGravatarId(), details.getUserId());
        return (rows > 0);
    }

    @Transactional
    public boolean updateEmailVerified(String userId, boolean verified) {
        String sql = "update from userdetails set emailverified = ?, lastupdated = now() where userid = ?";
        int rows = jdbcTemplate.update(sql, verified, userId);
        return (rows > 0);
    }

    public boolean isDisplayNameTaken(String displayName) {
        String sql = "select count(*) from userdetails where displayname = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, displayName);
        return (count > 0);
    }

    public int getReusedEmailCount(String email) {
        String sql = "select count(*) from userdetails where email = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, email);
    }
}
