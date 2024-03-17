package tv.memoryleakdeath.hex.backend.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import tv.memoryleakdeath.hex.common.pojo.BanAudit;

public class BanAuditMapper implements RowMapper<BanAudit> {

    @Override
    public BanAudit mapRow(ResultSet rs, int rowNum) throws SQLException {
        BanAudit ba = new BanAudit();
        ba.setAction(rs.getString("action"));
        ba.setCreatedDate(rs.getDate("created"));
        ba.setFollowerUserId(rs.getString("followeruserid"));
        ba.setId(rs.getString("id"));
        ba.setLastUpdatedDate(rs.getDate("lastupdated"));
        ba.setModeratorId(rs.getString("moderatorid"));
        ba.setReason(rs.getString("reason"));
        ba.setTargetUserId(rs.getString("targetuserid"));
        ba.setTimedOutExpirationDate(rs.getDate("timedoutexpiration"));
        return ba;
    }

}
