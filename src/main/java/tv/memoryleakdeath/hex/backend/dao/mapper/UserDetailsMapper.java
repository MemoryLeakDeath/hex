package tv.memoryleakdeath.hex.backend.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import tv.memoryleakdeath.hex.common.pojo.UserDetails;

public class UserDetailsMapper implements RowMapper<UserDetails> {

    @Override
    public UserDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserDetails details = new UserDetails();
        details.setDisplayName(rs.getString("displayname"));
        details.setEmail(rs.getString("email"));
        details.setEmailVerified(rs.getBoolean("emailverified"));
        details.setLastUpdated(rs.getDate("lastupdated"));
        details.setUserId(rs.getString("userid"));
        details.setGravatarId(rs.getString("gravatarid"));
        return details;
    }
}
