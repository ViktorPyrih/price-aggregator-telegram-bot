package ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.step.aggregator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.step.Step;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.domain.UserState;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.service.TelegramSenderService;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.util.Buttons;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.util.NumberUtils;

import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.CommonConstants.*;
import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.TelegramUtils.getChatId;

@Component
@RequiredArgsConstructor
public class ChooseMaxPriceStep implements Step {

    private static final String CHOOSE_MAX_PRICE_MESSAGE = "Choose max price, please";
    private static final String WRONG_MAX_PRICE_MESSAGE = " is not a valid number. Please, enter a valid number";
    private static final String WRONG_MAX_PRICE_MESSAGE_2 = "Max price should be greater than min price";

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
    public Result onStart(Update update, UserState userState) throws TelegramApiException {
        long chatId = getChatId(update);
        telegramSenderService.send(chatId, CHOOSE_MAX_PRICE_MESSAGE, Buttons.keyboard(BACK));

        return Result.of(userState);
    }

    @Override
    public Result process(Update update, UserState userState) throws TelegramApiException {
        long chatId = getChatId(update);

        String maxPrice = update.getMessage().getText();
        Double maxPriceValue = NumberUtils.parseDouble(maxPrice);
        if (maxPriceValue.isNaN()) {
            telegramSenderService.send(chatId, maxPrice + WRONG_MAX_PRICE_MESSAGE);
            return Result.of(userState);
        }

        double minPrice = Double.parseDouble(userState.getDataEntry(MIN_PRICE));
        if (minPrice > maxPriceValue) {
            telegramSenderService.send(chatId, WRONG_MAX_PRICE_MESSAGE_2);
            return Result.of(userState);
        }

        return Result.of(userState.nextStep().addDataEntry(MAX_PRICE, maxPrice));
    }

    @Override
    public Result processBack(Update update, UserState userState) throws TelegramApiException {
        onStart(update, userState);
        return Result.of(userState.removeDataEntry(MAX_PRICE));
    }
}
