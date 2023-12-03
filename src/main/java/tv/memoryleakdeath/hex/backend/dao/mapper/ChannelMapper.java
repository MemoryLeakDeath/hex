package tv.memoryleakdeath.hex.backend.dao.mapper;

import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import com.fasterxml.jackson.databind.ObjectMapper;

import tv.memoryleakdeath.hex.common.pojo.Channel;
import tv.memoryleakdeath.hex.common.pojo.ChannelSettings;

public class ChannelMapper implements RowMapper<Channel> {
    private static final Logger logger = LoggerFactory.getLogger(ChannelMapper.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Channel mapRow(ResultSet rs, int rowNum) throws SQLException {
        Channel channel = new Channel();
        channel.setActive(rs.getBoolean("active"));
        channel.setCreated(rs.getDate("created"));
        channel.setLastUpdated(rs.getDate("lastUpdated"));
        channel.setLive(rs.getBoolean("live"));
        channel.setName(rs.getString("name"));
        channel.setTitle(rs.getString("title"));
        channel.setUserId(rs.getString("userid"));
        ChannelSettings settings = null;
        try (InputStream in = rs.getBinaryStream("settings")) {
            if (!rs.wasNull()) {
                settings = objectMapper.readValue(in, ChannelSettings.class);
            }
        } catch (IOException e) {
            logger.error("Unable to parse json channel settings!", e);
        }
        channel.setSettings(settings);
        return channel;
    }

}
