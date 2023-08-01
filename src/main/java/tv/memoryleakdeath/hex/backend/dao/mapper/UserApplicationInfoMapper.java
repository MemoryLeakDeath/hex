package tv.memoryleakdeath.hex.backend.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import tv.memoryleakdeath.hex.common.pojo.UserApplicationInfo;

public class UserApplicationInfoMapper implements RowMapper<UserApplicationInfo> {

    @Override
    public UserApplicationInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserApplicationInfo info = new UserApplicationInfo();
        info.setApplicationId(rs.getString("registered_client_id"));
        info.setApplicationName(rs.getString("client_name"));
        info.setExpires(rs.getDate("expires"));
        info.setLastUsed(rs.getDate("last_used"));
        return info;
    }

}
