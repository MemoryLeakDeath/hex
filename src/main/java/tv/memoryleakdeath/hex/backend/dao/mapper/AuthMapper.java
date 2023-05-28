package tv.memoryleakdeath.hex.backend.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import tv.memoryleakdeath.hex.common.pojo.Auth;
import tv.memoryleakdeath.hex.common.pojo.TfaType;

public class AuthMapper implements RowMapper<Auth> {

    @Override
    public Auth mapRow(ResultSet rs, int rowNum) throws SQLException {
        Auth user = new Auth();
        user.setActive(rs.getBoolean("active"));
        user.setCreatedDate(rs.getDate("createddate"));
        user.setFailedAttempts(rs.getInt("failedattempts"));
        user.setId(rs.getString("id"));
        user.setLastAttemptedLogin(rs.getDate("lastattemptedlogin"));
        user.setPassword(rs.getString("password"));
        user.setRoles((String[]) rs.getArray("ROLES").getArray());
        user.setSecret(rs.getString("secret"));
        String tfaType = rs.getString("tfatype");
        if (tfaType != null) {
            user.setTfaType(TfaType.valueOf(tfaType));
        }
        user.setUsername(rs.getString("username"));
        user.setUseTfa(rs.getBoolean("usetfa"));
        return user;
    }

}
