package tv.memoryleakdeath.hex.frontend.controller.dashboard;

import java.awt.Dimension;
import java.io.IOException;

import org.apache.commons.imaging.ImageInfo;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.common.bytesource.ByteSource;
import org.apache.commons.imaging.common.bytesource.ByteSourceInputStream;
import org.apache.commons.imaging.formats.gif.GifImageParser;
import org.apache.commons.imaging.formats.jpeg.JpegImageParser;
import org.apache.commons.imaging.formats.png.PngImageParser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;

import com.github.bgalek.security.svg.SvgSecurityValidator;
import com.github.bgalek.security.svg.ValidationResult;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class EmoteValidator<T extends EmoteModel> {
    private static final Logger logger = LoggerFactory.getLogger(EmoteValidator.class);
    private static final long MAX_EMOTE_FILE_SIZE = 10L * 1024 * 1024; // 10MB
    private static final int MAX_EMOTE_WIDTH_PIXELS = 112;
    private static final int MAX_EMOTE_HEIGHT_PIXELS = 112;
    private static final int MAX_ANIMATED_GIF_FRAMES = 100;

    public void validate(HttpServletRequest request, T target, Errors errors) {
        if (StringUtils.isBlank(target.getName())) {
            errors.rejectValue("name", "emotemanage.text.error.namerequired");
        }

        if (StringUtils.isBlank(target.getTag())) {
            errors.rejectValue("tag", "emotemanage.text.error.tagrequired");
        }

        if (target.getSmallImage() == null) {
            errors.rejectValue("smallImage", "emotemanage.text.error.smallimagerequired");
        } else {
            // validate image size/properties
            if (!isFileSizeValid(target.getSmallImage())) {
                errors.rejectValue("smallImage", "emotemanage.text.error.imagefilesizewrong");
            }
            isFileContentsAllowed(target.getSmallImage(), "smallImage", errors);
        }

        if (target.getLargeImage() != null) {
            // large image should not be svg
            if ("image/svg+xml".equals(target.getLargeImage().getContentType())) {
                errors.rejectValue("largeImage", "emotemanage.text.error.largeimagecantbesvg");
            }

            // validate image size/properties
            if (!isFileSizeValid(target.getLargeImage())) {
                errors.rejectValue("largeImage", "emotemanage.text.error.imagefilesizewrong");
            }
            isFileContentsAllowed(target.getLargeImage(), "largeImage", errors);
        }
    }

    private boolean isFileSizeValid(MultipartFile file) {
        return (file.getSize() <= MAX_EMOTE_FILE_SIZE && !file.isEmpty());
    }

    private void isFileContentsAllowed(MultipartFile file, String fieldName, Errors errors) {
        String contentType = file.getContentType();
        if (contentType == null) {
            errors.rejectValue(fieldName, "emotemanage.text.error.unknownfile");
        }
        try {
            ByteSource imageByteSource = new ByteSourceInputStream(file.getInputStream(), file.getOriginalFilename());

            switch (contentType) {
            case "image/jpeg":
                JpegImageParser jpegParser = new JpegImageParser();
                Dimension jpegDimensions = jpegParser.getImageSize(imageByteSource, jpegParser.getDefaultParameters());
                if (jpegDimensions.getHeight() > MAX_EMOTE_HEIGHT_PIXELS
                        || jpegDimensions.getWidth() > MAX_EMOTE_WIDTH_PIXELS) {
                    errors.rejectValue(fieldName, "emotemanage.text.error.imagewrongdimensions");
                }
                break;
            case "image/gif":
                GifImageParser gifParser = new GifImageParser();
                Dimension gifDimensions = gifParser.getImageSize(imageByteSource, gifParser.getDefaultParameters());
                ImageInfo gifImageInfo = gifParser.getImageInfo(imageByteSource, gifParser.getDefaultParameters());
                if (gifDimensions.getHeight() > MAX_EMOTE_HEIGHT_PIXELS
                        || gifDimensions.getWidth() > MAX_EMOTE_WIDTH_PIXELS) {
                    errors.rejectValue(fieldName, "emotemanage.text.error.imagewrongdimensions");
                }
                if (gifImageInfo.getNumberOfImages() > MAX_ANIMATED_GIF_FRAMES) {
                    errors.rejectValue(fieldName, "emotemanage.text.error.animatedgiftoomanyframes");
                }
                break;
            case "image/png":
                PngImageParser pngParser = new PngImageParser();
                Dimension pngDimensions = pngParser.getImageSize(imageByteSource, pngParser.getDefaultParameters());
                if (pngDimensions.getHeight() > MAX_EMOTE_HEIGHT_PIXELS
                        || pngDimensions.getWidth() > MAX_EMOTE_WIDTH_PIXELS) {
                    errors.rejectValue(fieldName, "emotemanage.text.error.imagewrongdimensions");
                }
                break;
            case "image/svg+xml":
                SvgSecurityValidator svgValidator = SvgSecurityValidator.builder().build();
                ValidationResult svgValidationResult = svgValidator.validate(file.getBytes());
                if (svgValidationResult.hasViolations()) {
                    errors.rejectValue(fieldName, "emotemanage.text.error.svgimageinvalid");
                }
                break;
            default:
                errors.rejectValue(fieldName, "emotemanage.text.error.unknownfile");
                break;
            }
        } catch (ImageReadException | IOException e) {
            logger.error("Unable to read emote file!", e);
            errors.rejectValue(fieldName, "emotemanage.text.error.imagefileerror");
        }
    }
}
