package tv.memoryleakdeath.hex.frontend.controller.dashboard;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import tv.memoryleakdeath.hex.backend.dao.emote.ChannelEmotesDao;
import tv.memoryleakdeath.hex.common.pojo.ChannelEmote;
import tv.memoryleakdeath.hex.frontend.controller.BaseFrontendController;

@Controller
@RequestMapping("/dashboard")
public class StreamerDashboardController extends BaseFrontendController {
    private static final Logger logger = LoggerFactory.getLogger(StreamerDashboardController.class);
    private static final String EMOTE_UPLOAD_PHYSICAL_DIR_OFFSET = "/cust/emotes/";
    private static final String EMOTE_BASE_URL = "/cust/emotes/";

    @Autowired
    private ChannelEmotesDao channelEmotesDao;

    @Autowired
    private EmoteValidator<EmoteModel> emoteModelValidator;

    @Autowired
    @Qualifier("uploadsBaseDir")
    private String uploadsBaseDir;

    @GetMapping("/")
    public String view(HttpServletRequest request, Model model) {
        setPageTitle(request, model, "title.dashboard");
        setLayout(model, "layout/dashboard");
        return "dashboard/landingpage";
    }

    @GetMapping("/manageemotes")
    public String manageEmotes(HttpServletRequest request, Model model, EmoteModel emoteModel) {
        setPageTitle(request, model, "title.dashboard");
        setLayout(model, "layout/dashboard");
        model.addAttribute("emotePrefix", "");
        model.addAttribute("emoteModel", (emoteModel == null ? new EmoteModel() : emoteModel));
        return "dashboard/manageemotes";
    }

    @PostMapping("/addEmote")
    public String addEmote(HttpServletRequest request, Model model, @ModelAttribute EmoteModel emoteModel,
            BindingResult bindingResult) {
        emoteModelValidator.validate(request, emoteModel, bindingResult);
        if (bindingResult.hasErrors()) {
            logger.error("Validation errors attempting to add emote!");
            stuffBindingErrorsBackIntoModel("emoteModel", emoteModel, model, bindingResult);
            addErrorMessage(request, "text.error.systemerror");
            return manageEmotes(request, model, emoteModel);
        }
        try {
            String userId = getUserId(request);
            // TODO: get emote prefix
            ChannelEmote newChannelEmote = populateEmotePojo(emoteModel, userId, "tst");
            boolean fileCopySuccess = copyEmoteToFilesystem(request, emoteModel.getSmallImage(),
                    emoteModel.getLargeImage(), newChannelEmote);
            if (!fileCopySuccess) {
                logger.error("Unable to write new emote to file system! emote name: {} emote tag: {}",
                        emoteModel.getName(), emoteModel.getTag());
                addErrorMessage(request, "text.error.systemerror");
            } else {
                boolean success = channelEmotesDao.insertNewEmote(newChannelEmote);
                if (!success) {
                    logger.error("Unable to write new emote to database! emote name: {} emote tag: {}",
                            emoteModel.getName(), emoteModel.getTag());
                    addErrorMessage(request, "text.error.systemerror");
                } else {
                    addSuccessMessage(request, "text.success.newemotesaved");
                }
            }
        } catch (Exception e) {
            logger.error("Unable to write new emote to database with name: " + emoteModel.getName() + " and tag: "
                    + emoteModel.getTag(), e);
            addErrorMessage(request, "text.error.systemerror");
        }
        return "redirect:/dashboard/manageemotes";
    }

    private ChannelEmote populateEmotePojo(EmoteModel emoteModel, String userId, String prefix) {
        ChannelEmote emote = new ChannelEmote();
        emote.setActive(true);
        emote.setAllowed(emoteModel.getAllowed());
        emote.setAllowGlobal(emoteModel.getGlobal());
        emote.setApprovalStatus(null);
        emote.setSmallImageFilename(generateEmoteFilenameWithExtension(emoteModel.getSmallImage().getContentType()));
        emote.setSmallImageType(getImageTypeEnum(emoteModel.getSmallImage().getContentType()));
        emote.setSmallImageUrl(EMOTE_BASE_URL + userId + "/");
        if (emoteModel.getLargeImage() != null) {
            emote.setLargeImageFilename(
                    generateEmoteFilenameWithExtension(emoteModel.getLargeImage().getContentType()));
            emote.setLargeImageType(getImageTypeEnum(emoteModel.getLargeImage().getContentType()));
            emote.setLargeImageUrl(EMOTE_BASE_URL + userId + "/");
        }
        emote.setName(emoteModel.getName());
        emote.setPrefix(prefix);
        emote.setSubOnly(emoteModel.getSubscriberOnly());
        emote.setTag(emoteModel.getTag());
        emote.setUserId(userId);
        return emote;
    }

    private String generateEmoteFilenameWithExtension(String contentType) {
        String fileName = UUID.randomUUID().toString();
        String extension = "";
        switch (contentType) {
        case "image/jpeg":
            extension = ".jpg";
            break;
        case "image/gif":
            extension = ".gif";
            break;
        case "image/png":
            extension = ".png";
            break;
        case "image/svg+xml":
            extension = ".svg";
            break;
        default:
            break;
        }
        return (fileName + extension);
    }

    private String getImageTypeEnum(String contentType) {
        return switch (contentType) {
        case "image/jpeg" -> "jpeg";
        case "image/gif" -> "gif";
        case "image/png" -> "png";
        case "image/svg+xml" -> "svg";
        default -> null;
        };
    }

    private boolean copyEmoteToFilesystem(HttpServletRequest request, MultipartFile smallEmote, MultipartFile largeEmote, ChannelEmote emote) {
        ensureUserBucketDirectoryExists(emote.getUserId());
        String realPath = uploadsBaseDir + EMOTE_UPLOAD_PHYSICAL_DIR_OFFSET;
        String smallEmoteFullFilePath = "%s%s/%s".formatted(realPath, emote.getUserId(),
                emote.getSmallImageFilename());
        try {
            smallEmote.transferTo(new File(smallEmoteFullFilePath));
            if (largeEmote != null) {
                String largeEmoteFullFilePath = "%s%s/%s".formatted(realPath, emote.getUserId(),
                        emote.getLargeImageFilename());
                largeEmote.transferTo(new File(largeEmoteFullFilePath));
            }
        } catch (IllegalStateException | IOException e) {
            logger.error("Unable to transfer emote files to uploads directory!", e);
            return false;
        }
        return true;
    }

    private void ensureUserBucketDirectoryExists(String userId) {
        String realPath = uploadsBaseDir + EMOTE_UPLOAD_PHYSICAL_DIR_OFFSET;
        String userBucket = "%s%s/".formatted(realPath, userId);
        if (!new File(userBucket).exists()) {
            new File(userBucket).mkdirs();
        }
    }
}
