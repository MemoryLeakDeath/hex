package tv.memoryleakdeath.hex.backend.follower;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import tv.memoryleakdeath.hex.backend.dao.channel.ChannelsDao;
import tv.memoryleakdeath.hex.backend.dao.follower.FollowerDao;
import tv.memoryleakdeath.hex.common.ChannelEventType;
import tv.memoryleakdeath.hex.common.pojo.ChannelEvent;
import tv.memoryleakdeath.hex.common.pojo.Follower;

@Service
public class FollowerService {
    private static final Logger logger = LoggerFactory.getLogger(FollowerService.class);

    @Autowired
    private FollowerDao followerDao;

    @Autowired
    private ChannelsDao channelDao;

    @Autowired
    private ApplicationContext applicationContext;

    public boolean startFollowingChannel(String followerUserId, String channelName) {
        logger.debug("Attempting to follow channel: {} for user: {}", channelName, followerUserId);
        String channelId = channelDao.getChannelIdForName(channelName);
        boolean success = false;
        if (channelId != null) {
            if (followerDao.hasFollowingRecord(followerUserId, channelId)) {
                success = followerDao.activateFollower(followerUserId, channelId);
            } else {
                Follower follower = new Follower();
                follower.setActive(true);
                follower.setBanned(false);
                follower.setFollowerUserId(followerUserId);
                follower.setNotifications(true);
                follower.setTargetUserId(channelId);
                follower.setTimedOut(false);
                success = followerDao.insertNewFollower(follower);
            }
        }
        if (success) {
            ChannelEvent channelEvent = new ChannelEvent();
            channelEvent.setChannelName(channelName);
            channelEvent.setSourceUserId(followerUserId);
            channelEvent.setType(ChannelEventType.FOLLOW);
            applicationContext.publishEvent(channelEvent);
        }
        return success;
    }

    public boolean stopFollowingChannel(String followerUserId, String channelName) {
        logger.debug("Attempting to un-follow channel: {} for user: {}", channelName, followerUserId);
        boolean success = false;
        String channelId = channelDao.getChannelIdForName(channelName);
        if (channelId != null && followerDao.hasFollowingRecord(followerUserId, channelId)) {
            success = followerDao.deactivateFollower(followerUserId, channelId);
        }
        if (success) {
            ChannelEvent channelEvent = new ChannelEvent();
            channelEvent.setChannelName(channelName);
            channelEvent.setSourceUserId(followerUserId);
            channelEvent.setType(ChannelEventType.UNFOLLOW);
            applicationContext.publishEvent(channelEvent);
        }
        return success;
    }

    public boolean isCurrentlyFollowing(String followerUserId, String channelName) {
        String channelId = channelDao.getChannelIdForName(channelName);
        return followerDao.isActiveFollower(followerUserId, channelId);
    }
}
