package tv.memoryleakdeath.hex.backend.dao.follower;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import tv.memoryleakdeath.hex.backend.dao.mapper.BanAuditMapper;
import tv.memoryleakdeath.hex.common.pojo.BanAudit;

@Repository
public class BanAuditDao {
    private static final Logger logger = LoggerFactory.getLogger(BanAuditDao.class);
    private static final String[] COLUMNS = { "id", "targetuserid", "followeruserid", "moderatorid", "reason",
            "action", "created", "lastupdated", "timedoutexpiration" };
    private static final int SKIP_TYPE_COLUMNS = 4;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String INSERT_SQL = """
            insert into banaudits (%s) values(?::uuid, ?::uuid, ?::uuid, ?::uuid, %s)
            """.formatted(StringUtils.join(COLUMNS, ","),
            StringUtils.repeat("?", ",", COLUMNS.length - SKIP_TYPE_COLUMNS));

    @Transactional
    public boolean insert(BanAudit banAudit) {
        int rowsAffected = jdbcTemplate.update(INSERT_SQL, banAudit.getId(), banAudit.getTargetUserId(),
                banAudit.getFollowerUserId(), banAudit.getModeratorId(), banAudit.getReason(), banAudit.getAction(),
                banAudit.getCreatedDate(), banAudit.getLastUpdatedDate(), banAudit.getTimedOutExpirationDate());
        return (rowsAffected > 0);
    }

    @Transactional
    public boolean updateReason(String id, String reason) {
        if (StringUtils.isBlank(reason)) {
            return false;
        }
        String sql = "update banaudits set reason = ?, lastupdated = now() where id = ?::uuid";
        int rowsAffected = jdbcTemplate.update(sql, reason, id);
        return (rowsAffected > 0);
    }

    private static final String SHOW_BAN_AUDITS_FOR_USER_SQL = """
            select %s from banaudits where followeruserid = ?::uuid
            order by created desc
            """.formatted(StringUtils.join(COLUMNS, ","));
    private static final String SHOW_BAN_AUDITS_FOR_USER_AND_CHANNEL_SQL = """
            select %s from banaudits where targetuserid = ?::uuid and followeruserid = ?::uuid
            order by created desc
            """.formatted(StringUtils.join(COLUMNS, ","));
    private static final String SHOW_BAN_AUDITS_FOR_CHANNEL_SQL = """
            select %s from banaudits where targetuserid = ?::uuid
            order by created desc
            """.formatted(StringUtils.join(COLUMNS, ","));

    public List<BanAudit> findByUser(String userId) {
        return jdbcTemplate.query(SHOW_BAN_AUDITS_FOR_USER_SQL, new BanAuditMapper(), userId);
    }

    public List<BanAudit> findByUserAndChannel(String userId, String channelId) {
        return jdbcTemplate.query(SHOW_BAN_AUDITS_FOR_USER_AND_CHANNEL_SQL, new BanAuditMapper(), channelId, userId);
    }

    public List<BanAudit> findByChannel(String channelId) {
        return jdbcTemplate.query(SHOW_BAN_AUDITS_FOR_CHANNEL_SQL, new BanAuditMapper(), channelId);
    }
}
