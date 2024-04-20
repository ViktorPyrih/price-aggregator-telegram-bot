package ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.step;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.domain.UserState;

import java.util.Optional;

public interface Step {

    int flowId();

    int stepId();

    Optional<UserState> process(Update update, UserState userState) throws TelegramApiException;
}
