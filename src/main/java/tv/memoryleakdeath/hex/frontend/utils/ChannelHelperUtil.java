package tv.memoryleakdeath.hex.frontend.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import tv.memoryleakdeath.hex.backend.dao.channel.ChannelsDao;

@Component("ChannelHelperUtil")
public class ChannelHelperUtil {
    @Autowired
    private ChannelsDao channelsDao;

    public boolean hasChannel(String userId) {
        return channelsDao.hasChannel(userId);
    }

    public String getChannelName(String userId) {
        return channelsDao.getChannelName(userId);
    }
}
