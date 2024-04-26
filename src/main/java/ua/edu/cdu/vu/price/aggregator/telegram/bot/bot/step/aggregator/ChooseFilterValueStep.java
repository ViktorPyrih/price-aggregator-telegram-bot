package ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.step.aggregator;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.domain.UserState;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.service.PriceAggregatorService;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.service.TelegramSenderService;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.util.Buttons;

import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.CommonConstants.FILTER_KEY;
import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.TelegramUtils.getChatId;

@Component
public class ChooseFilterValueStep extends FilterStep {

    private static final String WRONG_FILTER_VALUE_MESSAGE = "Please, choose one of the following filter values: ";
    private static final String CHOOSE_FILTER_MESSAGE = "Choose filter values, please";

    private final TelegramSenderService telegramSenderService;

    public ChooseFilterValueStep(PriceAggregatorService priceAggregatorService, TelegramSenderService telegramSenderService) {
        super(priceAggregatorService);
        this.telegramSenderService = telegramSenderService;
    }

    @Override
    public int stepId() {
        return CHOOSE_FILTER_VALUE_STEP_ID;
    }

    @Override
    public Result process(Update update, UserState userState) throws TelegramApiException {
        long chatId = getChatId(update);

        var filters = getFilters(userState);
        String filterKey = update.getMessage().getText();
        var filterValues = extractValues(filters, filterKey);

        if (filterValues.isPresent()) {
            telegramSenderService.send(chatId, CHOOSE_FILTER_MESSAGE, Buttons.keyboard(filterValues.get(), true));
            return Result.of(userState.nextStep().addDataEntry(FILTER_KEY, filterKey));
        }

        telegramSenderService.send(chatId, WRONG_FILTER_VALUE_MESSAGE + String.join(", ", extractKeys(filters)));

        return Result.of(userState);
    }

    @Override
    public Result processBack(Update update, UserState userState) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Result processComplete(Update update, UserState userState) {
        return Result.of(CHOOSE_MIN_PRICE_STEP_ID, userState);
    }
}
