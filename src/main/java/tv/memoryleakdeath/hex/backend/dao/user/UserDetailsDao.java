package tv.memoryleakdeath.hex.backend.dao.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import tv.memoryleakdeath.hex.common.pojo.UserDetails;

@Repository
public class UserDetailsDao {
    private static final Logger logger = LoggerFactory.getLogger(UserDetailsDao.class);
    private static final String COLUMNS[] = { "userid", "displayname", "email", "emailverified", "lastupdated" };

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional
    public boolean insertInitialDetails(UserDetails details) {
        String sql = "insert into userdetails (userid, displayname, email, emailverified, lastupdated) values (?::uuid,?,?,false,now())";
        int rows = jdbcTemplate.update(sql, details.getUserId(), details.getDisplayName(), details.getEmail());
        return (rows > 0);
    }

    @Transactional
    public boolean updateDetails(UserDetails details) {
        String sql = "update from userdetails set displayname = ?, email = ?, lastupdated = now() where userid = ?";
        int rows = jdbcTemplate.update(sql, details.getDisplayName(), details.getEmail(), details.getUserId());
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
