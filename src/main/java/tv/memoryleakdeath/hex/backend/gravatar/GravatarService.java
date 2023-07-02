package tv.memoryleakdeath.hex.backend.gravatar;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("GravatarService")
public class GravatarService {
    private static final Logger logger = LoggerFactory.getLogger(GravatarService.class);
    private static final String GRAVATAR_BASE_IMAGE_URL = "https://www.gravatar.com/avatar/";

    public String getHash(String email) {
        String trimmedEmail = StringUtils.strip(email.toLowerCase());
        logger.debug("Generating gravatar hash for email: {}", trimmedEmail);
        return DigestUtils.md5Hex(trimmedEmail);
    }

    public String getAvatarUrl(String gravatarId) {
        String url = GRAVATAR_BASE_IMAGE_URL + gravatarId + "?s=40&d=robohash&r=g";
        return url;
    }
}
