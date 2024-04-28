package ua.edu.cdu.vu.price.aggregator.telegram.bot.util;

import lombok.experimental.UtilityClass;

import java.util.Optional;

@UtilityClass
public class NumberUtils {

    public static Optional<Integer> tryParseToInt(String value) {
        try {
            return Optional.of(Integer.parseInt(value));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public static Double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return Double.NaN;
        }
    }
}
