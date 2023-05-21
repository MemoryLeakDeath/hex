package tv.memoryleakdeath.hex.backend.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import tv.memoryleakdeath.hex.common.pojo.RememberMe;

public class RememberMeMapper implements RowMapper<RememberMe> {

    @Override
    public RememberMe mapRow(ResultSet rs, int rowNum) throws SQLException {
        RememberMe me = new RememberMe();
        me.setExpirationDate(rs.getDate("expirationdate"));
        me.setLastUsed(rs.getDate("lastused"));
        me.setToken(rs.getString("token"));
        me.setUserId(rs.getString("userid"));
        return me;
    }

}
