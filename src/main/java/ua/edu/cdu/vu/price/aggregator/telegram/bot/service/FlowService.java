package ua.edu.cdu.vu.price.aggregator.telegram.bot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.step.processor.StepProcessor;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.domain.UserState;

import java.util.function.UnaryOperator;

import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.TelegramUtils.getUserId;

@Service
@RequiredArgsConstructor
public class FlowService {

    private final StepProcessor stepProcessor;

    public void start(int id, Update update) throws TelegramApiException {
        start(id, update, UnaryOperator.identity());
    }

    public void start(int id, Update update, UnaryOperator<UserState> userStateCustomizer) throws TelegramApiException {
        long userId = getUserId(update);
        UserState userState = userStateCustomizer.apply(UserState.initial(userId, id));
        stepProcessor.process(userState, update);
    }
}
