package tv.memoryleakdeath.hex.backend.dao.security;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import tv.memoryleakdeath.hex.backend.dao.mapper.AuthMapper;
import tv.memoryleakdeath.hex.common.pojo.Auth;
import tv.memoryleakdeath.hex.common.pojo.TfaType;

@Repository
public class AuthenticationDao {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationDao.class);
    private static final String[] COLUMN_NAMES = { "id", "username", "password", "active", "failedattempts", "usetfa", "tfatype", "secret", "createddate", "lastattemptedlogin" };

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String GET_USER_BY_USERNAME = """
                select %s, ARRAY(select authority from authorities a where a.userid = id) AS roles from identities
                where username = ?
            """.formatted(StringUtils.join(COLUMN_NAMES, ","));

    public Auth getUserByUsername(String username) {
        List<Auth> results = jdbcTemplate.query(GET_USER_BY_USERNAME, new AuthMapper(), username);
        if (results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }

    private static final String GET_ACTIVE_USER_BY_ID = """
                select %s, ARRAY(select authority from authorities where id = ?::uuid) AS roles from identities
                where id = ?::uuid and active = true
            """.formatted(StringUtils.join(COLUMN_NAMES, ","));

    public Auth getActiveUserById(String id) {
        List<Auth> results = jdbcTemplate.query(GET_ACTIVE_USER_BY_ID, new AuthMapper(), id, id);
        if (results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }

    public String getUserIdForUsername(String username) {
        String sql = "select id from identities where username = ?";
        return jdbcTemplate.query(sql, new ResultSetExtractor<String>() {
            @Override
            public String extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) {
                    return rs.getString("id");
                }
                return null;
            }
        }, username);
    }

    public boolean checkUsernameExists(String username) {
        String sql = "select count(*) from identities where username = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, username);
        return (count > 0);
    }

    @Transactional
    public String createUserInitial(String username, String password) {
        logger.debug("New user created! Username: {}", username);
        String sql = "insert into identities (username, password, active) values (?,?,true)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, username);
                ps.setString(2, password);
                return ps;
            }
        }, keyHolder);
        String id = keyHolder.getKeys().get("id").toString();
        createInitalUserRole(id);
        return id;
    }

    private void createInitalUserRole(String id) {
        String sql = "insert into authorities (userid, authority) values (?::uuid,'ROLE_USER')";
        jdbcTemplate.update(sql, id);
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

    @Transactional
    public boolean updateFailedAttempts(String username, boolean reset) {
        String incrementSql = "update identities set failedattempts = failedattempts + 1, lastattemptedlogin = now() where username = ?";
        String resetSql = "update identities set failedattempts = 0, lastattemptedlogin = now() where username = ?";
        int rows = 0;
        if (reset) {
            rows = jdbcTemplate.update(resetSql, username);
        } else {
            rows = jdbcTemplate.update(incrementSql, username);
        }
        return (rows == 1);
    }

    @Transactional
    public boolean updateLastAttemptDate(String username) {
        String sql = "update identities set lastattemptedlogin = now() where username = ?";
        int rows = jdbcTemplate.update(sql, username);
        return (rows == 1);
    }

    @Transactional
    public boolean updatePassword(String userId, String password) {
        String sql = "update identities set password = ? where id = ?::uuid";
        int rows = jdbcTemplate.update(sql, password, userId);
        return (rows == 1);
    }
}
