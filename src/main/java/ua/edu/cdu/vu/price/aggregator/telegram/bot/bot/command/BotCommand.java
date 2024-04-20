package ua.edu.cdu.vu.event.notification.telegram.bot.component.command;

import lombok.Builder;
import lombok.Value;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface BotCommand {

    String ROOT = "/";
    String START = "/start";
    String HELP = "/help";
    String CREATE = "/create";
    String DELETE = "/delete";
    String UPDATE = "/update";
    String GET_ALL = "/all";

    @Value
    @Builder
    class Result {

        String response;

        public static Result empty() {
            return Result.builder().build();
        }

    }

    Result execute(Update update) throws TelegramApiException;

    default Long getUserId(Update update) {
        return update.hasCallbackQuery() ? update.getCallbackQuery().getFrom().getId() : update.getMessage().getFrom().getId();
    }
}
