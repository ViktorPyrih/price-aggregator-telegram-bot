package ua.edu.cdu.vu.price.aggregator.telegram.bot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.step.processor.StepProcessor;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.domain.UserStateService;

import java.util.List;
import java.util.function.Consumer;

import static java.util.Objects.nonNull;
import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.TelegramUtils.getChatId;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramBotService implements Consumer<List<Update>> {

    private static final String COMMAND_PREFIX = "/";

    private final BotCommandService botCommandService;
    private final StepProcessor stepProcessor;
    private final UserStateService userStateService;
    private final TelegramSenderService telegramSenderService;

    public void accept(List<Update> updates) {
        Update update = updates.getLast();
        try {
            if (update.hasCallbackQuery() || isCommand(update)) {
                botCommandService.processCommand(update);
            } else if (nonNull(update.getMessage().getText())) {
                var userState = userStateService.findUserState(update.getMessage().getFrom().getId());
                if (userState.isPresent()) {
                    stepProcessor.process(update, userState.get());
                }
            }
        } catch (TelegramApiException e) {
            log.error("Request to Telegram API failed", e);
        } catch (RuntimeException e) {
            log.error("Internal error occurred", e);
            telegramSenderService.sendUnchecked(getChatId(update), "Something went wrong on the server side. The error was logged and will be fixed soon");
        }
    }

    private boolean isCommand(Update update) {
        return update.getMessage().getText().startsWith(COMMAND_PREFIX);
    }
}
