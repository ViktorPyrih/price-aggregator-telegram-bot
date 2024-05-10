package ua.edu.cdu.vu.price.aggregator.telegram.bot.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.SPACE;

@UtilityClass
public class CallbackDataUtils {

    private static final int SEARCH_QUERY_MAX_LENGTH = 40;
    private static final String SEARCH_QUERY_PATTERN = "[^\\s\\w]";

    public static String extractSearchQuery(@NonNull String title) {
        String query = StringUtils.substringBefore(title, "(")
                .replaceAll(SEARCH_QUERY_PATTERN, EMPTY)
                .replaceAll("\\s{2,}", SPACE)
                .trim();

        var parts = query.split(SPACE);
        StringBuilder builder = new StringBuilder();
        for (String part: parts) {
            builder.append(part);
            if (Base64Utils.encode(builder.toString()).length() > SEARCH_QUERY_MAX_LENGTH) {
                builder.delete(builder.length() - part.length(), builder.length());
                break;
            }
            builder.append(SPACE);
        }

        return builder.toString().trim();
    }
}
