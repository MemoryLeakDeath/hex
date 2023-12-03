package tv.memoryleakdeath.hex.backend.dao.chat;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import tv.memoryleakdeath.hex.backend.dao.mapper.ChatMessageMapper;
import tv.memoryleakdeath.hex.common.pojo.ChatMessage;

@Repository
public class ChatMessageDao {
    private static final Logger logger = LoggerFactory.getLogger(ChatMessageDao.class);
    private static final String[] COLUMNS = { "messageid", "senderid", "channelid", "created", "lastupdated", "visible",
            "message", "details" };

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final String INSERT_MESSAGE_SQL = """
            insert into chatmessages (%s) values(?::uuid, ?::uuid, ?::uuid, NOW(), NOW(), ?, ?, ?::jsonb)
            """.formatted(StringUtils.join(COLUMNS, ","));

    public ChatMessage createChatMessage(final ChatMessage chatMessage) {
        if (chatMessage == null) {
            return null;
        }
        ChatMessage savedChatMessage;
        try {
            savedChatMessage = (ChatMessage) chatMessage.clone();
        } catch (CloneNotSupportedException e) {
            logger.error("Unable to clone chat message!", e);
            return null;
        }
        String detailsJson = null;
        if (chatMessage.getDetails() != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                detailsJson = objectMapper.writeValueAsString(chatMessage.getDetails());
            } catch (JsonProcessingException e) {
                logger.error("Unable to parse chat message details into a JSON string for saving!", e);
                return null;
            }
        }
        String messageId = UUID.randomUUID().toString();
        savedChatMessage.setMessageId(messageId);
        Date now = new Date();
        savedChatMessage.setCreated(now);
        savedChatMessage.setLastUpdated(now);
        jdbcTemplate.update(INSERT_MESSAGE_SQL, messageId, chatMessage.getSenderId(),
                chatMessage.getChannelId(), chatMessage.getVisible(), chatMessage.getMessage(), detailsJson);
        return savedChatMessage;
    }

    private static final String SELECT_LAST_CHANNEL_MESSAGES_SQL = """
            select %s from chatmessages where channelid = ?::uuid and visible = true
            order by created asc
            fetch first 5 rows only
            """.formatted(StringUtils.join(COLUMNS, ","));

    public List<ChatMessage> getLastMessagesForChannel(String channelId) {
        return jdbcTemplate.query(SELECT_LAST_CHANNEL_MESSAGES_SQL, new ChatMessageMapper(), channelId);
    }
}
