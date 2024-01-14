package tv.memoryleakdeath.hex.backend.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import tv.memoryleakdeath.hex.common.pojo.UserDetailsDisplay;

public class UserDetailsDisplayMapper implements RowMapper<UserDetailsDisplay> {

    @Override
    public UserDetailsDisplay mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserDetailsDisplay display = new UserDetailsDisplay();
        display.setDisplayName(rs.getString("displayname"));
        display.setGravatarId(rs.getString("gravatarid"));
        return display;
    }

}
