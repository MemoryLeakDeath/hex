package tv.memoryleakdeath.hex.backend.dao.email;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class EmailVerificationDao {
    private static final Logger logger = LoggerFactory.getLogger(EmailVerificationDao.class);
    private static final String[] COLUMNS = { "userid", "token", "expirationdate" };

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean isVerified(String userId, String token) {
        if (userId == null || token == null) {
            return false;
        }
        String sql = "select count(*) from emailverification where userid = ?::uuid and token = ? and expirationdate < now()";
        int result = jdbcTemplate.queryForObject(sql, Integer.class, userId, token);
        return (result == 1);
    }

    @Transactional
    public void deleteExpired() {
        String sql = "delete from emailverification where expirationdate < now()";
        int rows = jdbcTemplate.update(sql);
        logger.info("[Clean Email Verification Tokens] Removed {} expired tokens", rows);
    }

    @Transactional
    public boolean createNewToken(String userId, String token, Date expirationDate) {
        String sql = "insert into emailverification (userid, token, expirationdate) values (?::uuid,?,?)";
        int rows = jdbcTemplate.update(sql, userId, token, expirationDate);
        return (rows > 0);
    }
}
