package ua.edu.cdu.vu.event.notification.telegram.bot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.edu.cdu.vu.event.notification.telegram.bot.component.command.BotCommand;
import ua.edu.cdu.vu.event.notification.telegram.bot.component.command.parser.BotCommandParser;
import ua.edu.cdu.vu.event.notification.telegram.bot.component.command.resolver.BotCommandResolver;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BotCommandService {

    private final BotCommandResolver botCommandResolver;
    private final BotCommandParser botCommandParser;
    private final TelegramSenderService telegramSenderService;

    public void processCommand(Update update) throws TelegramApiException {
        BotCommand.Result result;
        if (update.hasCallbackQuery()) {
            String command = botCommandParser.parseCommand(update.getCallbackQuery().getData());
            result = botCommandResolver.resolve(command).execute(update);
        } else {
            result = botCommandResolver.resolve(update.getMessage().getText()).execute(update);
        }

        processResult(update, result);
    }

    private void processResult(Update update, BotCommand.Result result) throws TelegramApiException {
        long chatId = getChatId(update);
        if (Objects.nonNull(result.getResponse())) {
            telegramSenderService.send(chatId, result.getResponse());
        }
    }

    private long getChatId(Update update) {
        return update.hasCallbackQuery() ? update.getCallbackQuery().getMessage().getChatId() : update.getMessage().getChatId();
    }
}
