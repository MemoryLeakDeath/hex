package tv.memoryleakdeath.hex.backend.chat;

import java.util.List;

import org.apache.commons.text.StringTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import tv.memoryleakdeath.hex.common.pojo.ChatMessage;

@Service
public class ChatMessageProcessingService {
    private static final Logger logger = LoggerFactory.getLogger(ChatMessageProcessingService.class);
    private static final String EMOTE_DELIM = ":";

    public void process(ChatMessage message) {
        List<String> tokenizedMessage = tokenizeMessage(message);
        parseEmotes(tokenizedMessage, message);
    }

    private List<String> tokenizeMessage(ChatMessage message) {
        StringTokenizer tokenizer = new StringTokenizer(message.getMessage());
        return tokenizer.getTokenList();
    }

    private void parseEmotes(List<String> tokenizedMessage, ChatMessage message) {
        message.getDetails().setEmoteList(
                tokenizedMessage.stream().filter(s -> s.startsWith(EMOTE_DELIM) && s.endsWith(EMOTE_DELIM)).toList());
    }
}
