package tv.memoryleakdeath.hex.backend.dao.security;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import tv.memoryleakdeath.hex.backend.dao.mapper.AuthMapper;
import tv.memoryleakdeath.hex.common.pojo.Auth;

@Repository
public class AuthenticationDao {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationDao.class);
    private static final String[] COLUMN_NAMES = { "id", "username", "password", "active" };

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String GET_USER_BY_USERNAME = """
                select %s, ARRAY(select authority from authorities where username = ?) AS roles from users
                where username = ?
            """.formatted(StringUtils.join(COLUMN_NAMES, ","));

    public Auth getUserByUsername(String username) {
        return jdbcTemplate.queryForObject(GET_USER_BY_USERNAME, new AuthMapper(), username, username);
    }
}
