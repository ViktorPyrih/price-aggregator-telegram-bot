package ua.edu.cdu.vu.event.notification.telegram.bot.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.edu.cdu.vu.event.notification.telegram.bot.domain.UserState;
import ua.edu.cdu.vu.event.notification.telegram.bot.component.step.processor.StepProcessor;

import java.util.Optional;

import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramBotService {

    private static final String SOMETHING_WENT_WRONG = "Something went wrong on the server side. Please try again";
    private static final String COMMAND_PREFIX = "/";

    private final BotCommandService botCommandService;
    private final StepProcessor stepProcessor;
    private final UserStateService userStateService;
    private final TelegramSenderService telegramSenderService;

    @SneakyThrows
    public void process(Update update) {
        try {
            if (update.hasCallbackQuery() || isCommand(update)) {
                botCommandService.processCommand(update);
            } else if (nonNull(update.getMessage().getText())) {
                Optional<UserState> userState = userStateService.findUserState(update.getMessage().getFrom().getId());
                if (userState.isPresent()) {
                    stepProcessor.process(userState.get(), update);
                }
            }
        } catch (TelegramApiException e) {
            log.error("Request to Telegram API failed", e);
        } catch (RuntimeException e) {
            log.error("Internal error occurred", e);
            telegramSenderService.send(getChatId(update), SOMETHING_WENT_WRONG);
        }
    }

    private boolean isCommand(Update update) {
        return update.getMessage().getText().startsWith(COMMAND_PREFIX);
    }

    private long getChatId(Update update) {
        return update.hasCallbackQuery() ? update.getCallbackQuery().getMessage().getChatId() : update.getMessage().getChatId();
    }
}
