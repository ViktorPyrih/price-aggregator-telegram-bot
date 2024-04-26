package ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.step.aggregator;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.step.Step;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.domain.UserState;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.service.TelegramSenderService;

import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.CommonConstants.AGGREGATOR_FLOW_ID;
import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.TelegramUtils.getChatId;

@Component
@RequiredArgsConstructor
public class ChooseMaxPriceStep implements Step {

    private static final String CHOOSE_MAX_PRICE_MESSAGE = "Choose max price, please";
    private static final String WRONG_MIN_PRICE_MESSAGE = " is not a number. Please, enter a number";

    private final TelegramSenderService telegramSenderService;

    @Override
    public int flowId() {
        return AGGREGATOR_FLOW_ID;
    }

    @Override
    public int stepId() {
        return CHOOSE_MAX_PRICE_STEP_ID;
    }

    @Override
    public Result process(Update update, UserState userState) throws TelegramApiException {
        long chatId = getChatId(update);

        String minPrice = update.getMessage().getText();
        if (NumberUtils.isParsable(minPrice)) {
            telegramSenderService.send(chatId, CHOOSE_MAX_PRICE_MESSAGE);
            return Result.of(userState.nextStep().addDataEntry("minPrice", minPrice));
        }

        telegramSenderService.send(chatId, minPrice + WRONG_MIN_PRICE_MESSAGE);

        return Result.of(userState);
    }

    @Override
    public Result processBack(Update update, UserState userState) throws TelegramApiException {
        long chatId = getChatId(update);
        telegramSenderService.send(chatId, CHOOSE_MAX_PRICE_MESSAGE);

        return Result.of(userState.nextStep());
    }
}
