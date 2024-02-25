package tv.memoryleakdeath.hex.backend.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.StringTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tv.memoryleakdeath.hex.common.ChatTokenType;
import tv.memoryleakdeath.hex.common.pojo.ChatMessage;
import tv.memoryleakdeath.hex.common.pojo.ChatToken;

@Service
public class ChatMessageProcessingService {
    private static final Logger logger = LoggerFactory.getLogger(ChatMessageProcessingService.class);
    private static final String EMOTE_DELIM = ":";

    @Autowired
    private ChatMessageEmoteProcessingService emoteProcessingService;

    public void process(ChatMessage message) {
        List<String> tokenizedMessage = tokenizeMessage(message);
        identifyChatTokens(tokenizedMessage, message);
        emoteProcessingService.parseEmotes(message);
        String htmlMessage = convertMessageToHtml(message);
        message.getDetails().setProcessedMessage(StringEscapeUtils.escapeHtml4(htmlMessage));
    }

    private List<String> tokenizeMessage(ChatMessage message) {
        StringTokenizer tokenizer = new StringTokenizer(message.getMessage());
        return tokenizer.getTokenList();
    }

    private void identifyChatTokens(List<String> tokenizedMessage, ChatMessage message) {
        List<ChatToken> chatTokens = new ArrayList<>();
        for (String token : tokenizedMessage) {
            ChatToken newToken = Stream.<Supplier<ChatToken>>of(() -> identifyEmoteToken(token)).map(Supplier::get)
                    .filter(Objects::nonNull)
                    .findFirst().orElseGet(() -> identifyNormalToken(token));
            chatTokens.add(newToken);
        }
        message.getDetails().setTokenList(chatTokens);
    }

    private ChatToken identifyEmoteToken(String token) {
        String emoteToken = StringUtils.substringBetween(token, EMOTE_DELIM);
        if (emoteToken != null) {
            ChatToken thisToken = new ChatToken();
            thisToken.setParsedToken(emoteToken);
            thisToken.setRawToken(token);
            thisToken.setType(ChatTokenType.EMOTE);
            return thisToken;
        }
        return null;
    }

    private ChatToken identifyNormalToken(String token) {
        ChatToken thisToken = new ChatToken();
        thisToken.setParsedToken(token);
        thisToken.setRawToken(token);
        thisToken.setType(ChatTokenType.NORMAL);
        return thisToken;
    }

    private String convertMessageToHtml(ChatMessage message) {
        StringBuilder sb = new StringBuilder();
        for (ChatToken token : message.getDetails().getTokenList()) {
            switch (token.getType()) {
            case EMOTE:
                String tokenHtml = emoteProcessingService.generateHtml(token, message);
                sb.append(" ").append(tokenHtml);
                break;
            case NORMAL:
                sb.append(" ").append(token.getRawToken());
                break;
            default:
                break;
            }
        }
        return sb.toString();
    }

}
