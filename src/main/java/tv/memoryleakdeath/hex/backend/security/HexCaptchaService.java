package tv.memoryleakdeath.hex.backend.security;

import java.awt.Color;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import net.logicsquad.nanocaptcha.audio.AudioCaptcha;
import net.logicsquad.nanocaptcha.audio.noise.RandomNoiseProducer;
import net.logicsquad.nanocaptcha.audio.producer.RandomNumberVoiceProducer;
import net.logicsquad.nanocaptcha.content.LatinContentProducer;
import net.logicsquad.nanocaptcha.content.NumbersContentProducer;
import net.logicsquad.nanocaptcha.image.ImageCaptcha;
import net.logicsquad.nanocaptcha.image.backgrounds.SquigglesBackgroundProducer;
import net.logicsquad.nanocaptcha.image.noise.CurvedLineNoiseProducer;

@Service
public class HexCaptchaService {
    private static final Logger logger = LoggerFactory.getLogger(HexCaptchaService.class);
    private static final Color CAPTCHA_LINE_COLOR = Color.GREEN;
    private static final float CAPTCHA_LINE_WIDTH = 0.56f;
    public static final int CAPTCHA_WIDTH = 200;
    public static final int CAPTCHA_HEIGHT = 50;
    public static final int CAPTCHA_LENGTH = 7;
    
    public ImageCaptcha generateImageCaptcha() {
        return new ImageCaptcha.Builder(CAPTCHA_WIDTH, CAPTCHA_HEIGHT)
                .addContent(new LatinContentProducer(CAPTCHA_LENGTH))
                .addNoise(new CurvedLineNoiseProducer(CAPTCHA_LINE_COLOR, CAPTCHA_LINE_WIDTH))
                .addBackground(new SquigglesBackgroundProducer())
                .build();
    }

    public boolean verifyImageCaptcha(ImageCaptcha captcha, String answer) {
        boolean correct = captcha.isCorrect(answer);
        logger.debug("[ImageCaptcha] Verifying image captcha result: {}", correct);
        return correct;
    }

    public AudioCaptcha generateAudioCaptcha() {
        return new AudioCaptcha.Builder()
                .addContent(new NumbersContentProducer(CAPTCHA_LENGTH))
                .addVoice(new RandomNumberVoiceProducer(Locale.ENGLISH))
                .addNoise(new RandomNoiseProducer())
                .build();
    }

    public boolean verifyAudioCaptcha(AudioCaptcha captcha, String answer) {
        boolean correct = captcha.isCorrect(answer);
        logger.debug("[AudioCaptcha] Verifying audio captcha result: {}", correct);
        return correct;
    }
}
