package ua.edu.cdu.vu.price.aggregator.telegram.bot.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.apache.commons.codec.binary.Base64;

import java.nio.charset.StandardCharsets;

@UtilityClass
public class Base64Utils {

    public static String encode(@NonNull String data) {
        return Base64.encodeBase64String(data.getBytes(StandardCharsets.UTF_8));
    }

    public static byte[] decode(@NonNull String data) {
        return Base64.decodeBase64(data);
    }

    public static String decodeAsString(@NonNull String data) {
        return new String(decode(data), StandardCharsets.UTF_8);
    }
}
