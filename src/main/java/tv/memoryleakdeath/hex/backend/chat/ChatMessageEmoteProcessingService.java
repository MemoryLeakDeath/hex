package tv.memoryleakdeath.hex.backend.chat;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tv.memoryleakdeath.hex.backend.dao.emote.ChannelEmotesDao;
import tv.memoryleakdeath.hex.common.ChatTokenType;
import tv.memoryleakdeath.hex.common.EmoteImageTypeConstants;
import tv.memoryleakdeath.hex.common.pojo.ChannelEmote;
import tv.memoryleakdeath.hex.common.pojo.ChatMessage;
import tv.memoryleakdeath.hex.common.pojo.ChatToken;
import tv.memoryleakdeath.hex.common.pojo.MessageEmote;

@Service
public class ChatMessageEmoteProcessingService {
    private static final Logger logger = LoggerFactory.getLogger(ChatMessageEmoteProcessingService.class);
    private static final int[] LARGE_IMAGE_SIZE = { 112, 112 };
    private static final int[] SMALL_IMAGE_SIZE = { 28, 28 };

    @Autowired
    private ChannelEmotesDao emotesDao;

    public void parseEmotes(ChatMessage message) {
        List<ChatToken> emoteTokens = message.getDetails().getTokenList().stream()
                .filter(t -> t.getType() == ChatTokenType.EMOTE).toList();
        if (emoteTokens.isEmpty()) {
            return;
        }
        Set<String> parsedEmotes = emoteTokens.stream().map(ChatToken::getParsedToken)
                .collect(Collectors.toUnmodifiableSet());
        List<ChannelEmote> emotes = emotesDao.getAllActiveChannelApprovedEmotes(parsedEmotes);
        message.getDetails().setEmoteList(emotes.stream().map(e -> createMessageEmoteObject(e)).toList());
    }

    private MessageEmote createMessageEmoteObject(ChannelEmote emote) {
        MessageEmote messageEmote = new MessageEmote();
        messageEmote.setActive(emote.isActive());
        messageEmote.setAllowed(emote.isAllowed());
        messageEmote.setAllowGlobal(emote.getAllowGlobal());
        messageEmote.setCreated(emote.getCreated());
        messageEmote.setId(emote.getId());
        messageEmote.setLargeImageFilename(emote.getLargeImageFilename());
        messageEmote.setLargeImageType(emote.getLargeImageType());
        messageEmote.setLargeImageUrl(emote.getLargeImageUrl());
        messageEmote.setLastUpdated(emote.getLastUpdated());
        messageEmote.setPrefix(emote.getPrefix());
        messageEmote.setSmallImageFilename(emote.getSmallImageFilename());
        messageEmote.setSmallImageType(emote.getSmallImageType());
        messageEmote.setSmallImageUrl(emote.getSmallImageUrl());
        messageEmote.setSubOnly(emote.getSubOnly());
        messageEmote.setTag(emote.getTag());
        messageEmote.setUserId(emote.getUserId());
        return messageEmote;
    }

    public String generateHtml(ChatToken token, ChatMessage message) {
        if (message.getDetails().getEmoteList().isEmpty()) {
            logger.error("No emotes to generate HTML for!");
            return "";
        }
        int totalNumTokens = message.getDetails().getTokenList().size();
        MessageEmote messageEmoteDetails = message.getDetails().getEmoteList().stream()
                .filter(d -> d.isMatching(token.getParsedToken())).findFirst().orElse(null);
        if (messageEmoteDetails == null) {
            logger.error("Can't find matching emote details for emote: {}", token.getRawToken());
            return "";
        }
        String url = determineImageUrl(messageEmoteDetails, totalNumTokens);
        boolean useSmallImageSize = isUseSmallImageSize(messageEmoteDetails, totalNumTokens);
        int width = LARGE_IMAGE_SIZE[0];
        int height = LARGE_IMAGE_SIZE[1];
        if (useSmallImageSize) {
            width = SMALL_IMAGE_SIZE[0];
            height = SMALL_IMAGE_SIZE[1];
        }
        String html = """
                <img src="%s" width="%d" height="%d" class="chat-emote">
                """.formatted(url, width, height);
        logger.debug("generated html for emote: {} -- html: {}", token.getRawToken(), html);
        return html;
    }

    private String determineImageUrl(MessageEmote messageEmoteDetails, int numTokens) {
        String url = "";
        if (isUseSmallImageSize(messageEmoteDetails, numTokens)) {
            url = messageEmoteDetails.getSmallImageUrl() + messageEmoteDetails.getSmallImageFilename();
        } else {
            url = messageEmoteDetails.getLargeImageUrl() + messageEmoteDetails.getLargeImageFilename();
        }
        return url;
    }

    private boolean isUseSmallImageSize(MessageEmote messageEmoteDetails, int numTokens) {
        return (numTokens > 1
                || EmoteImageTypeConstants.SVG_EMOTE_TYPE.equals(messageEmoteDetails.getSmallImageType()));
    }

}
