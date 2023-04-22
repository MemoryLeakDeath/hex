package tv.memoryleakdeath.hex.backend.dao.security;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import tv.memoryleakdeath.hex.backend.dao.mapper.AuthMapper;
import tv.memoryleakdeath.hex.common.pojo.Auth;
import tv.memoryleakdeath.hex.common.pojo.TfaType;

@Repository
public class AuthenticationDao {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationDao.class);
    private static final String[] COLUMN_NAMES = { "id", "username", "password", "active", "failedattempts", "emailverified", "usetfa", "tfatype", "secret", "createddate", "lastattemptedlogin" };

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String GET_USER_BY_USERNAME = """
                select %s, ARRAY(select authority from authorities where username = ?) AS roles from identities
                where username = ?
            """.formatted(StringUtils.join(COLUMN_NAMES, ","));

    public Auth getUserByUsername(String username) {
        return jdbcTemplate.queryForObject(GET_USER_BY_USERNAME, new AuthMapper(), username, username);
    }

    public boolean checkUsernameExists(String username) {
        String sql = "select count(*) from identities where username = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, username);
        return (count > 0);
    }

    @Transactional
    public void createUserInitial(String username, String password) {
        logger.debug("New user created! Username: {}", username);
        String sql = "insert into identities (username, password, active) values (?,?,true)";
        jdbcTemplate.update(sql, username, password);
        createInitalUserRole(username);
    }

    private void createInitalUserRole(String username) {
        String sql = "insert into authorities (username, authority) values (?,'ROLE_USER')";
        jdbcTemplate.update(sql, username);
    }

    @Transactional
    public boolean updateUserTfa(String username, Boolean useTfa, String secret, TfaType type) {
        String sql;
        int rowsUpdated = 0;
        if (StringUtils.isNotBlank(secret) && type != null) {
            sql = "update identities set usetfa = ?, tfatype = ?::tfa_types, secret = ? where username = ?";
            rowsUpdated = jdbcTemplate.update(sql, useTfa, type.name(), secret, username);
        } else {
            sql = "update identities set usetfa = ? where username = ?";
            rowsUpdated = jdbcTemplate.update(sql, useTfa, username);
        }
        return (rowsUpdated > 0);
    }

    @Transactional
    public boolean updateUserTfa(String username, Boolean useTfa) {
        return updateUserTfa(username, useTfa, null, null);
    }
}
