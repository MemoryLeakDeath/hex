package tv.memoryleakdeath.hex.backend.dao.mapper;

import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import com.fasterxml.jackson.databind.ObjectMapper;

import tv.memoryleakdeath.hex.common.pojo.ChatMessage;
import tv.memoryleakdeath.hex.common.pojo.ChatMessageDetails;

public class ChatMessageMapper implements RowMapper<ChatMessage> {
    private static final Logger logger = LoggerFactory.getLogger(ChatMessageMapper.class);
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ChatMessage mapRow(ResultSet rs, int rowNum) throws SQLException {
        ChatMessage message = new ChatMessage();
        message.setChannelId(rs.getString("channelid"));
        message.setCreated(rs.getDate("created"));
        message.setLastUpdated(rs.getDate("lastUpdated"));
        message.setMessage(rs.getString("message"));
        message.setMessageId(rs.getString("messageid"));
        message.setSenderId(rs.getString("senderid"));
        message.setVisible(rs.getBoolean("visible"));
        ChatMessageDetails details = null;
        try (InputStream is = rs.getBinaryStream("details")) {
            if (!rs.wasNull()) {
                details = objectMapper.readValue(is, ChatMessageDetails.class);
            }
        } catch (IOException | SQLException e) {
            logger.error("Unable to parse json details from database!", e);
        }
        message.setDetails(details);
        return message;
    }

}
