package ua.edu.cdu.vu.price.aggregator.telegram.bot.util;

import lombok.experimental.UtilityClass;
import org.telegram.telegrambots.meta.api.objects.Update;

@UtilityClass
public class TelegramUtils {

    public static long getChatId(Update update) {
        return update.hasCallbackQuery() ? update.getCallbackQuery().getMessage().getChatId() : update.getMessage().getChatId();
    }

    public static long getUserId(Update update) {
        return update.hasCallbackQuery() ? update.getCallbackQuery().getFrom().getId() : update.getMessage().getFrom().getId();
    }
}
