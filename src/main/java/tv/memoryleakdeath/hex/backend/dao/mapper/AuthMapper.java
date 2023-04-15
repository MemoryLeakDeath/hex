package tv.memoryleakdeath.hex.backend.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import tv.memoryleakdeath.hex.common.pojo.Auth;

public class AuthMapper implements RowMapper<Auth> {

    @Override
    public Auth mapRow(ResultSet rs, int rowNum) throws SQLException {
        Auth user = new Auth();
        user.setActive(rs.getBoolean("active"));
        user.setId(rs.getString("id"));
        user.setPassword(rs.getString("password"));
        user.setUsername(rs.getString("username"));
        user.setRoles((String[]) rs.getArray("ROLES").getArray());
        return user;
    }

}
