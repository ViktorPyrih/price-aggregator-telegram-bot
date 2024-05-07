package ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.command;

import lombok.Builder;
import lombok.Value;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface BotCommand {

    String ROOT = "/";
    String START = "/start";
    String HELP = "/help";

    String SEARCH = "/search";

    @Value
    @Builder
    class Result {

        String response;

        public boolean isEmpty() {
            return response == null;
        }

        public static Result empty() {
            return Result.builder().build();
        }

    }

    Result execute(Update update) throws TelegramApiException;
}
