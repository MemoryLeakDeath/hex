package tv.memoryleakdeath.hex.utils;

import java.util.regex.Pattern;

public final class ChannelUtils {
    private static final Pattern channelNamePattern = Pattern.compile("[^\\w-]",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

    private ChannelUtils() {

    }

    public static String convertToChannelName(String displayName) {
        return channelNamePattern.matcher(displayName).replaceAll("");
    }
}
