package ua.edu.cdu.vu.price.aggregator.telegram.bot.util;

import lombok.experimental.UtilityClass;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@UtilityClass
public class ImageUtils {

    public static byte[] decode(String image) {
        return Base64.getDecoder().decode(image.getBytes(StandardCharsets.UTF_8));
    }
}
