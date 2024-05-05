package ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.step.aggregator;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.domain.Filter;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.domain.UserState;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.service.PriceAggregatorService;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.service.TelegramSenderService;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.util.Buttons;

import java.util.List;

import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.CommonConstants.FILTER_KEY;
import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.CommonConstants.FILTER_KEY_PREFIX;
import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.TelegramUtils.getChatId;

@Component
public class ChooseFilterKeyStep extends FilterStep {

    private static final String CHOOSE_FILTER_MESSAGE = "Choose a filter, please";
    private static final String WRONG_FILTER_MESSAGE = "Please, choose one of the following filters: ";
    private static final String RESET_FILTERS_MESSAGE = "Resetting filters...";

    public ChooseFilterKeyStep(PriceAggregatorService priceAggregatorService, TelegramSenderService telegramSenderService) {
        super(priceAggregatorService, telegramSenderService);
    }

    @Override
    public int stepId() {
        return CHOOSE_FILTER_KEY_STEP_ID;
    }

    @Override
    public Result onStart(Update update, UserState userState) throws TelegramApiException {
        long chatId = getChatId(update);

        var filters = getFilters(userState);
        telegramSenderService.sendMessage(chatId, CHOOSE_FILTER_MESSAGE, Buttons.keyboard(extractKeys(filters), true, true, true));

        return Result.of(userState);
    }

    @Override
    public Result process(Update update, UserState userState) throws TelegramApiException {
        long chatId = getChatId(update);

        var filters = extractKeys(getFilters(userState));
        String filter = update.getMessage().getText();

        if (filters.contains(filter)) {
            return Result.of(userState.nextStep().addDataEntry(FILTER_KEY, filter));
        }

        telegramSenderService.sendMessage(chatId, WRONG_FILTER_MESSAGE + String.join(", ", filters));

        return Result.of(userState);
    }

    @Override
    public Result processBack(Update update, UserState userState) throws TelegramApiException {
        onStart(update, userState);
        return Result.of(userState.removeDataEntry(FILTER_KEY));
    }

    @Override
    public Result processReset(Update update, UserState userState) throws TelegramApiException {
        long chatId = getChatId(update);
        telegramSenderService.sendMessage(chatId, RESET_FILTERS_MESSAGE);
        return Result.of(userState.removeDataEntriesByPrefix(FILTER_KEY_PREFIX));
    }

    private List<String> extractKeys(List<Filter> filters) {
        return filters.stream()
                .map(Filter::getKey)
                .toList();
    }
}
