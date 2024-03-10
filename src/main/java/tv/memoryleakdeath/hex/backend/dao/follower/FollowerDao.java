package tv.memoryleakdeath.hex.backend.dao.follower;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import tv.memoryleakdeath.hex.common.pojo.Follower;

@Repository
public class FollowerDao {
    private static final Logger logger = LoggerFactory.getLogger(FollowerDao.class);
    private static final String[] COLUMN_NAMES = { "targetuserid", "followeruserid", "active", "banned", "timedout",
            "notifications", "created", "lastupdated", "timedoutexpiration" };

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String INSERT_FOLLOWER_SQL = """
            insert into followers (%s) values (?::uuid, ?::uuid, %s)
            """.formatted(StringUtils.join(COLUMN_NAMES, ","), StringUtils.repeat("?", ",", COLUMN_NAMES.length - 2));

    @Transactional
    public boolean insertNewFollower(Follower follower) {
        logger.debug("Inserting new follower: {} for channel: {}", follower.getFollowerUserId(),
                follower.getTargetUserId());
        follower.setCreatedDate(new Date());
        follower.setLastUpdatedDate(new Date());
        int rowsAffected = jdbcTemplate.update(INSERT_FOLLOWER_SQL, follower.getTargetUserId(),
                follower.getFollowerUserId(), follower.getActive(), follower.getBanned(), follower.getTimedOut(),
                follower.getNotifications(), follower.getCreatedDate(), follower.getLastUpdatedDate(),
                follower.getTimedOutExpirationDate());
        return (rowsAffected > 0);
    }

    @Transactional
    public boolean deactivateFollower(String followerId, String channelId) {
        String sql = "update followers set active = false, lastupdated = now() where targetuserid = ?::uuid and followeruserid = ?::uuid";
        int rowsAffected = jdbcTemplate.update(sql, channelId, followerId);
        return (rowsAffected > 0);
    }

    @Transactional
    public boolean activateFollower(String followerId, String channelId) {
        String sql = "update followers set active = true, lastupdated = now() where targetuserid = ?::uuid and followeruserid = ?::uuid";
        int rowsAffected = jdbcTemplate.update(sql, channelId, followerId);
        return (rowsAffected > 0);
    }

    public boolean hasFollowingRecord(String followerId, String channelId) {
        String sql = "select COUNT(*) from followers where targetuserid = ?::uuid and followeruserid = ?::uuid";
        int result = jdbcTemplate.queryForObject(sql, Integer.class, channelId, followerId);
        return (result > 0);
    }

    public boolean isActiveFollower(String followerId, String channelId) {
        String sql = "select COUNT(*) from followers where targetuserid = ?::uuid and followeruserid = ?::uuid and active = true";
        int result = jdbcTemplate.queryForObject(sql, Integer.class, channelId, followerId);
        return (result > 0);
    }

    public boolean isBanned(String followerId, String channelId) {
        String sql = "select COUNT(*) from followers where targetuserid = ?::uuid and followeruserid = ?::uuid and banned = true";
        int result = jdbcTemplate.queryForObject(sql, Integer.class, channelId, followerId);
        return (result > 0);
    }

    public boolean isTimedOut(String followerId, String channelId) {
        String sql = """
                select COUNT(*) from followers
                where targetuserid = ?::uuid and followeruserid = ?::uuid
                and timedout = true and (timedoutexpiration is not null and timedoutexpiration > now())
                """;
        int result = jdbcTemplate.queryForObject(sql, Integer.class, channelId, followerId);
        return (result > 0);
    }
}
